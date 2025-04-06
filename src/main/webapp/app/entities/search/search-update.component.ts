import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import SearchService from './search.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { type ISearch, Search } from '@/shared/model/search.model';
import ParameterUpdate from '@/entities/parameter/parameter-update.vue';
import FilterUpdate from '@/entities/filter/filter-update.vue';
import Comment from '@/entities/comment/comment.vue';

export default defineComponent({
  components: {
    comment: Comment,
    'filter-update': FilterUpdate,
    'parameter-update': ParameterUpdate,
  },
  compatConfig: { MODE: 3 },
  name: 'SearchUpdate',
  setup() {
    const searchService = inject('searchService', () => new SearchService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const search: Ref<ISearch> = ref(new Search());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
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

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {},
      favorite: {},
      createDate: {},
      filter: {},
      parameter: {},
      comments: {},
      user: {},
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
      users,
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
