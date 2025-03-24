import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import SearchService from './search.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ISearch, Search } from '@/shared/model/search.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SearchUpdate',
  setup() {
    const searchService = inject('searchService', () => new SearchService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const search: Ref<ISearch> = ref(new Search());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveSearch = async searchId => {
      try {
        const res = await searchService().find(searchId);
        res.createDate = new Date(res.createDate);
        search.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.searchId) {
      retrieveSearch(route.params.searchId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {},
      favorite: {},
      createDate: {},
    };
    const v$ = useVuelidate(validationRules, search as any);
    v$.value.$validate();

    return {
      searchService,
      alertService,
      search,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: search }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.search.id) {
        this.searchService()
          .update(this.search)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('pampaInsightsApp.search.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.searchService()
          .create(this.search)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('pampaInsightsApp.search.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
