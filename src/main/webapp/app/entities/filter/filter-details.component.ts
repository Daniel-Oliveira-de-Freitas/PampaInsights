import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import FilterService from './filter.service';
import { type IFilter } from '@/shared/model/filter.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FilterDetails',
  setup() {
    const filterService = inject('filterService', () => new FilterService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const filter: Ref<IFilter> = ref({});

    const retrieveFilter = async filterId => {
      try {
        const res = await filterService().find(filterId);
        filter.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.filterId) {
      retrieveFilter(route.params.filterId);
    }

    return {
      alertService,
      filter,

      previousState,
      t$: useI18n().t,
    };
  },
});
