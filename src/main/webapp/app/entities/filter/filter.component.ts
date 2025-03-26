import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import FilterService from './filter.service';
import { type IFilter } from '@/shared/model/filter.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Filter',
  setup() {
    const { t: t$ } = useI18n();
    const filterService = inject('filterService', () => new FilterService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const filters: Ref<IFilter[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveFilters = async () => {
      isFetching.value = true;
      try {
        const res = await filterService().retrieve();
        filters.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveFilters();
    };

    onMounted(async () => {
      await retrieveFilters();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IFilter) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeFilter = async () => {
      try {
        await filterService().delete(removeId.value);
        const message = t$('pampaInsightsApp.filter.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveFilters();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      filters,
      handleSyncList,
      isFetching,
      retrieveFilters,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeFilter,
      t$,
    };
  },
});
