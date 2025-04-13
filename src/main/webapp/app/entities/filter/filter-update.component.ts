import { computed, defineComponent, inject, onMounted, type PropType, type Ref, ref } from 'vue';
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

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FilterUpdate',
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

    const searches: Ref<ISearch[]> = ref([]);
    const visualizationValues: Ref<string[]> = ref(Object.keys(Visualization));
    const typeOfChartValues: Ref<string[]> = ref(Object.keys(TypeOfChart));
    const emotionsValues: Ref<string[]> = ref(Object.keys(Emotions));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);
    const showSidebar = ref(true);
    const route = useRoute();
    const router = useRouter();
    const isEditing = ref(false);

    const previousState = () => router.go(-1);

    const refreshState = () => router.go(0);

    const retrieveFilter = async filterId => {
      try {
        filter.value = await filterService().findBySearchId(filterId);
      } catch (error) {
        console.log('Pesquisa sem Filtros');
      }
    };

    onMounted(async () => {
      await retrieveFilter(props.searchId);
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
      name: {},
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

    return {
      filterService,
      alertService,
      filter,
      previousState,
      refreshState,
      visualizationValues,
      typeOfChartValues,
      emotionsValues,
      isSaving,
      currentLanguage,
      searches,
      toggleSidebar,
      closeSidebar,
      showSidebar,
      isEditing,
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
            this.refreshState();
            this.alertService.showInfo(this.t$('pampaInsightsApp.filter.updated', { param: param.id }));
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
            this.refreshState();
            this.alertService.showSuccess(this.t$('pampaInsightsApp.filter.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
    typeChart() {
      eventBus.emit('search-comments');
    },

    toggleEdit() {
      this.isEditing = !this.isEditing;
    },
  },
});
