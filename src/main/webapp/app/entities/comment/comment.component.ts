import { computed, defineComponent, inject, onMounted, ref, type Ref } from 'vue';
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
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const commentService = inject('commentService', () => new CommentService());
    const commentsCollectorService = inject('commentsCollectorService', () => new CommentsCollectorService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const comments: Ref<IComment[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveComments = async () => {
      isFetching.value = true;
      try {
        const res = await commentService().retrieve();
        comments.value = res.data;
        eventBus.emit('sentiment-data', sentimentData.value);
      } catch (err: any) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const retrieveCommentsApi = async (payload: { urls: string[]; keyword: string | null }) => {
      isFetching.value = true;
      try {
        const res = await commentsCollectorService().retrieveCommentApi(payload);
        comments.value = res;
        console.log('comments', res);
        eventBus.emit('sentiment-data', sentimentData.value);
      } catch (err: any) {
        alertService.showHttpError(err?.response ?? { data: { message: err.message }, status: 500 });
      } finally {
        isFetching.value = false;
      }
    };

    onMounted(() => {
      eventBus.on('analyze-request', async (payload: { urls: string[]; keyword: string | null }) => {
        console.log('Received analyze request:', payload);
        if (payload.urls.length > 0) {
          await retrieveCommentsApi(payload);
        }
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
      retrieveComments,
      clear,
      sentimentData,
      chartOptions,
      ...dateFormat,
      t$,
    };
  },
});
