import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

import SearchService from './search.service';
import { type ISearch } from '@/shared/model/search.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Search',
  setup() {
    const { t: t$ } = useI18n();
    const router = useRouter();
    const dateFormat = useDateFormat();
    const searchService = inject('searchService', () => new SearchService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const searches: Ref<ISearch[]> = ref([]);
    const isFetching = ref(false);

    const retrieveSearches = async () => {
      isFetching.value = true;
      try {
        const res = await searchService().retrieve();
        searches.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveSearches();
    };

    onMounted(async () => {
      await retrieveSearches();
    });

    const removeId: Ref<number | null> = ref(null);
    const removeEntity = ref<any>(null);

    const prepareRemove = (instance: ISearch) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };

    const closeDialog = () => {
      removeEntity.value.hide();
    };

    const removeSearch = async () => {
      try {
        await searchService().delete(removeId.value);
        alertService.showInfo(t$('pampaInsightsApp.search.deleted', { param: removeId.value }), { variant: 'danger' });
        removeId.value = null;
        retrieveSearches();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const toggleFavorite = (search: ISearch) => {
      search.favorite = !search.favorite;
      searchService()
        .update(search)
        .catch(err => {
          alertService.showHttpError(err.response);
        });
    };

    const goToEdit = (searchId: number | undefined) => {
      router.push({ name: 'SearchEdit', params: { searchId } });
    };

    return {
      searches,
      handleSyncList,
      isFetching,
      retrieveSearches,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeSearch,
      toggleFavorite,
      goToEdit,
      t$,
      ...dateFormat,
    };
  },
});
