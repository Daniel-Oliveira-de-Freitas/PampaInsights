import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import CommentService from './comment.service';
import { type IComment } from '@/shared/model/comment.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Comment',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const commentService = inject('commentService', () => new CommentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const comments: Ref<IComment[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveComments = async () => {
      isFetching.value = true;
      try {
        const res = await commentService().retrieve();
        comments.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveComments();
    };

    onMounted(async () => {
      await retrieveComments();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IComment) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeComment = async () => {
      try {
        await commentService().delete(removeId.value);
        const message = t$('pampaInsightsApp.comment.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveComments();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      comments,
      handleSyncList,
      isFetching,
      retrieveComments,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeComment,
      t$,
    };
  },
});
