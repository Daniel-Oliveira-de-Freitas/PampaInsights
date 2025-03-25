import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ParameterService from './parameter.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import SearchService from '@/entities/search/search.service';
import { type ISearch } from '@/shared/model/search.model';
import { type IParameter, Parameter } from '@/shared/model/parameter.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ParameterUpdate',
  setup() {
    const parameterService = inject('parameterService', () => new ParameterService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const parameter: Ref<IParameter> = ref(new Parameter());

    const searchService = inject('searchService', () => new SearchService());

    const searches: Ref<ISearch[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveParameter = async parameterId => {
      try {
        const res = await parameterService().find(parameterId);
        res.createDate = new Date(res.createDate);
        parameter.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.parameterId) {
      retrieveParameter(route.params.parameterId);
    }

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
      terms: {},
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

    return {
      parameterService,
      alertService,
      parameter,
      previousState,
      isSaving,
      currentLanguage,
      searches,
      v$,
      ...useDateFormat({ entityRef: parameter }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.parameter.id) {
        this.parameterService()
          .update(this.parameter)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('pampaInsightsApp.parameter.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.parameterService()
          .create(this.parameter)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('pampaInsightsApp.parameter.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
