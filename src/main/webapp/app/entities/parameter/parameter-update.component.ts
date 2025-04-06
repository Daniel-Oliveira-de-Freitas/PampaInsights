import { computed, defineComponent, inject, onMounted, type PropType, type Ref, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ParameterService from './parameter.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import SearchService from '@/entities/search/search.service';
import { type ISearch } from '@/shared/model/search.model';
import { type IParameter, Parameter } from '@/shared/model/parameter.model';
import { required } from '@vuelidate/validators';
import type { IComment } from '@/shared/model/comment.model.ts';
import eventBus from '../../../../../event-bus.ts';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ParameterUpdate',
  props: {
    searchId: {
      type: [Number] as PropType<number | undefined>,
    },
  },
  setup(props) {
    const parameterService = inject('parameterService', () => new ParameterService());
    const commentService = inject('commentService', () => new CommentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const parameter: Ref<IParameter> = ref(new Parameter());

    const searchService = inject('searchService', () => new SearchService());

    const searches: Ref<ISearch[]> = ref([]);
    const comments: Ref<IComment[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);
    const showSidebar = ref(false);
    const route = useRoute();
    const router = useRouter();
    const isEditing = ref(false);
    const isFetching = ref(false);

    const previousState = () => router.go(-1);

    const retrieveParameter = async (searchId: number | undefined) => {
      try {
        parameter.value = await parameterService().findBySearchId(searchId);
      } catch (error) {
        alertService.showHttpError((error as any).response);
      }
    };

    retrieveParameter(props.searchId);
    isEditing.value = !parameter.value.id;

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
      terms: { required },
      webSite: {},
      instagram: {},
      facebook: {},
      linkedin: {},
      x: {},
      createDate: {},
      search: {},
    };
    const v$ = useVuelidate(validationRules, parameter as any);
    v$.value.$validate();

    const toggleSidebar = () => {
      showSidebar.value = !showSidebar.value;
    };

    const closeSidebar = () => {
      showSidebar.value = false;
    };

    return {
      parameterService,
      commentService,
      alertService,
      parameter,
      previousState,
      isSaving,
      isFetching,
      currentLanguage,
      searches,
      comments,
      toggleSidebar,
      closeSidebar,
      showSidebar,
      validations,
      isEditing,
      v$,
      ...useDateFormat({ entityRef: parameter }),
      t$,
    };
  },
  created(): void {},
  methods: {
    async save() {
      this.isSaving = true;
      if (this.parameter.id) {
        try {
          const param = await this.parameterService().update(this.parameter);
          this.alertService.showInfo(this.t$('pampaInsightsApp.parameter.updated', { param: param.id }));
          this.previousState();
        } catch (error) {
          this.alertService.showHttpError(error.response);
        } finally {
          this.isSaving = false;
        }
      } else {
        try {
          const param = await this.parameterService().create(this.parameter, this.searchId);
          this.alertService.showSuccess(this.t$('pampaInsightsApp.parameter.created', { param: param.id }).toString());
          this.previousState();
        } catch (error) {
          this.alertService.showHttpError(error.response);
        } finally {
          this.isSaving = false;
        }
      }
    },

    searchComments() {
      eventBus.emit('search-comments');
    },

    toggleEdit() {
      this.isEditing = !this.isEditing;
    },
  },
});
