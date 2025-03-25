import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CommentService from './comment.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import SearchService from '@/entities/search/search.service';
import { type ISearch } from '@/shared/model/search.model';
import { Comment, type IComment } from '@/shared/model/comment.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CommentUpdate',
  setup() {
    const commentService = inject('commentService', () => new CommentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const comment: Ref<IComment> = ref(new Comment());

    const searchService = inject('searchService', () => new SearchService());

    const searches: Ref<ISearch[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveComment = async commentId => {
      try {
        const res = await commentService().find(commentId);
        res.createDate = new Date(res.createDate);
        comment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.commentId) {
      retrieveComment(route.params.commentId);
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
      body: {},
      sentiment: {},
      author: {},
      createDate: {},
      search: {},
    };
    const v$ = useVuelidate(validationRules, comment as any);
    v$.value.$validate();

    return {
      commentService,
      alertService,
      comment,
      previousState,
      isSaving,
      currentLanguage,
      searches,
      v$,
      ...useDateFormat({ entityRef: comment }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.comment.id) {
        this.commentService()
          .update(this.comment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('pampaInsightsApp.comment.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.commentService()
          .create(this.comment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('pampaInsightsApp.comment.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
