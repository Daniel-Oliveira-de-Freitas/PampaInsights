import { computed, defineComponent, inject, onMounted, onUnmounted, type PropType, type Ref, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import FilterService from './filter.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import SearchService from '@/entities/search/search.service';
import { type ISearch } from '@/shared/model/search.model';
import { Filter, type IFilter } from '@/shared/model/filter.model';
import { Visualization } from '@/shared/model/enumerations/visualization.model';
import { TypeOfChart } from '@/shared/model/enumerations/type-of-chart.model';
import { Emotions } from '@/shared/model/enumerations/emotions.model';
import eventBus from '../../../../../event-bus.ts';
import { ArcElement, BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, Tooltip } from 'chart.js';
import { Bar, Pie } from 'vue-chartjs';
import { SentimentAnalysisType } from '@/shared/model/enumerations/sentiment-analysis-type.model.ts';

ChartJS.register(ArcElement, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

export default defineComponent({
  computed: {
    TypeOfChart() {
      return TypeOfChart;
    },
  },
  compatConfig: { MODE: 3 },
  name: 'FilterUpdate',
  components: {
    PieChart: Pie as any,
    BarChart: Bar as any,
  },
  props: {
    searchId: {
      type: [Number] as PropType<number | undefined>,
    },
  },
  setup(props) {
    const filterService = inject('filterService', () => new FilterService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const filter: Ref<IFilter> = ref(new Filter());

    const searchService = inject('searchService', () => new SearchService());
    const selectedChartData: any = ref(null);
    const appliedTypeOfChart = ref<TypeOfChart | null>(null);

    const searches: Ref<ISearch[]> = ref([]);
    const visualizationValues: Ref<string[]> = ref(Object.keys(Visualization));
    const typeOfChartValues: Ref<string[]> = ref(Object.keys(TypeOfChart));
    const sentimentAnalysisTypeValues: Ref<string[]> = ref(Object.keys(SentimentAnalysisType));
    const emotionsValues: Ref<string[]> = ref(Object.keys(Emotions));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);
    const showSidebar = ref(true);
    const route = useRoute();
    const router = useRouter();
    const isEditing = ref(true);
    const showChart = ref(false);

    const previousState = () => router.go(-1);

    const retrieveFilter = async (filterId: any) => {
      try {
        filter.value = ((await filterService().findBySearchId(filterId)) as IFilter) ?? new Filter();
        if (filter.value.id) {
          isEditing.value = false;
        }
      } catch (error) {
        console.log('Pesquisa sem Filtros');
      } finally {
        if (!filter.value.sentimentAnalysisType) {
          filter.value.sentimentAnalysisType = SentimentAnalysisType.EMOTION_POLARITY;
        }
        if (!filter.value.visualization) {
          filter.value.visualization = Visualization.ALL as any;
        }
        if (!filter.value.emotions) {
          filter.value.emotions = Emotions.ALL as any;
        }
        if (!filter.value.typeOfChart) {
          filter.value.typeOfChart = TypeOfChart.PIZZA as any;
        }
      }
    };

    const onSentimentData = (data: any) => {
      selectedChartData.value = data;
      if (data) showChart.value = true;
    };

    onMounted(async () => {
      eventBus.on('sentiment-data', onSentimentData);
      await retrieveFilter(props.searchId);
      applyFilters();
    });

    onUnmounted(() => {
      eventBus.off('sentiment-data', onSentimentData);
    });

    const initRelationships = () => {
      searchService()
        .retrieve()
        .then(res => {
          searches.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      sentimentAnalysisType: {},
      visualization: {},
      typeOfChart: {},
      emotions: {},
      search: {},
    };
    const v$ = useVuelidate(validationRules, filter as any);
    v$.value.$validate();

    const toggleSidebar = () => {
      showSidebar.value = !showSidebar.value;
    };

    const closeSidebar = () => {
      showSidebar.value = false;
    };

    const applyFilters = () => {
      appliedTypeOfChart.value = v$.value.typeOfChart.$model as TypeOfChart | null;
      showChart.value = true;
      eventBus.emit('apply-emotions-filter', v$.value.emotions.$model ?? Emotions.ALL);
    };

    return {
      filterService,
      alertService,
      filter,
      previousState,
      visualizationValues,
      typeOfChartValues,
      emotionsValues,
      isSaving,
      currentLanguage,
      searches,
      toggleSidebar,
      closeSidebar,
      applyFilters,
      showSidebar,
      selectedChartData,
      appliedTypeOfChart,
      sentimentAnalysisTypeValues,
      showChart,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.filter.id) {
        this.filterService()
          .update(this.filter)
          .then(param => {
            this.isSaving = false;
            this.filter = param;
            // this.alertService.showInfo(this.t$('pampaInsightsApp.filter.updated', { param: param.id }));
            this.applyFilters();
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.filterService()
          .create(this.filter, this.searchId)
          .then(param => {
            this.isSaving = false;
            this.filter = param;
            // this.alertService.showSuccess(this.t$('pampaInsightsApp.filter.created', { param: param.id }).toString());
            this.applyFilters();
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
