import { computed, defineComponent, inject, onMounted, type PropType, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import CommentService from './comment.service';
import { type IComment } from '@/shared/model/comment.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import { Pie } from 'vue-chartjs';
import { ArcElement, Chart as ChartJS, Legend, Tooltip } from 'chart.js';
import eventBus from '../../../../../event-bus.ts';
import CommentsCollectorService from '@/entities/comments-collector/comments-collector.service.ts';

ChartJS.register(ArcElement, Tooltip, Legend);

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Comment',
  components: {
    PieChart: Pie as any,
  },
  props: {
    searchId: {
      type: [Number] as PropType<number | undefined>,
    },
  },
  setup(props) {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const commentService = inject('commentService', () => new CommentService());
    const commentsCollectorService = inject('commentsCollectorService', () => new CommentsCollectorService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const comments: Ref<IComment[]> = ref([]);
    const isFetching = ref(false);

    const clear = () => {};

    const retrieveCommentsBySearchId = async (searchId: any) => {
      isFetching.value = true;
      try {
        const res = await commentService().retrieveCommentsBySearchId(searchId);
        comments.value = res.data;
      } catch (err: any) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    onMounted(() => {
      eventBus.off('searchComments');
      eventBus.on('searchComments', async () => {
        await retrieveCommentsBySearchId(props.searchId);
      });

      eventBus.off('requestSentimentData');
      eventBus.on('requestSentimentData', async () => {
        await retrieveCommentsBySearchId(props.searchId);
        eventBus.emit('sentimentData', sentimentData.value);
      });
    });

    const sentimentCounts = computed(() => {
      let positive = 0,
        negative = 0,
        neutral = 0;
      comments.value.forEach(comment => {
        if (comment.sentiment > 0) positive++;
        else if (comment.sentiment < 0) negative++;
        else neutral++;
      });
      return { positive, negative, neutral };
    });

    const sentimentData = computed(() => ({
      labels: ['Positivo', 'Negativo', 'Neutro'],
      datasets: [
        {
          data: [sentimentCounts.value.positive, sentimentCounts.value.negative, sentimentCounts.value.neutral],
          backgroundColor: ['green', 'red', 'gray'],
        },
      ],
    }));

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
    };

    return {
      comments,
      isFetching,
      retrieveCommentsBySearchId,
      // retrieveCommentsApi,
      clear,
      sentimentData,
      chartOptions,
      ...dateFormat,
      t$,
    };
  },
});
