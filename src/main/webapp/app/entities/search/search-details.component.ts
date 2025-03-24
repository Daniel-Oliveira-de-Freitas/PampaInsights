import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import SearchService from './search.service';
import { useDateFormat } from '@/shared/composables';
import { type ISearch } from '@/shared/model/search.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SearchDetails',
  setup() {
    const dateFormat = useDateFormat();
    const searchService = inject('searchService', () => new SearchService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const search: Ref<ISearch> = ref({});

    const retrieveSearch = async searchId => {
      try {
        const res = await searchService().find(searchId);
        search.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.searchId) {
      retrieveSearch(route.params.searchId);
    }

    return {
      ...dateFormat,
      alertService,
      search,

      previousState,
      t$: useI18n().t,
    };
  },
});
