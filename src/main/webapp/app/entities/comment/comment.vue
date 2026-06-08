<template>
  <div class="mt-5">
    <div class="d-flex align-items-center gap-3 mb-1" id="page-heading" data-cy="CommentHeading">
      <h2 class="mb-0" v-text="t$('pampaInsightsApp.comment.home.title')"></h2>
      <span v-if="filteredComments.length > 0" class="badge rounded-pill bg-secondary px-3 py-2" style="font-size: 1rem; font-weight: 600">
        {{ filteredComments.length }}
      </span>
    </div>
    <div v-if="isFetching" class="alert alert-info mt-3 d-flex align-items-center gap-3" role="status" aria-live="polite">
      <span class="spinner-border spinner-border flex-shrink-0 mr-2" aria-hidden="true"></span>
      <span>{{ loadingMessage }}</span>
    </div>
    <div class="alert alert-info mt-3" v-if="!isFetching && filteredComments && filteredComments.length === 0">
      <span v-text="t$('pampaInsightsApp.comment.home.notFound')"></span>
    </div>
    <div v-if="filteredComments && filteredComments.length > 0" class="comments-container mt-5 overflow-auto p-2" style="max-height: 70vh">
      <div v-for="comment in filteredComments" :key="comment.id" class="comment-bubble">
        <div class="comment-content">
          <p class="comment-body">{{ comment.body }}</p>
          <span class="date">{{ formatDateShort(comment.createDate) || '' }}</span>
        </div>
        <div class="comment-meta align-items-end">
          <div class="sentiment-icon">
            <font-awesome-icon v-if="comment.sentiment > 0" icon="fa-solid fa-smile" class="positive" />
            <font-awesome-icon v-else-if="comment.sentiment < 0" icon="fa-solid fa-frown" class="negative" />
            <font-awesome-icon v-else icon="fa-solid fa-meh" class="neutral" />
          </div>
          <span class="author">{{ comment.author || 'Desconhecido' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./comment.component.ts"></script>

<style scoped>
.comments-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-bubble {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: #eaf6f6;
  border-left: 4px solid #18bc9c;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.07);
  max-width: 100%;
  position: relative;
}

.comment-content {
  flex-grow: 1;
}

.comment-body {
  font-size: 16px;
  margin-bottom: 4px;
  color: #333;
}

.date {
  font-size: 12px;
  color: gray;
}

.comment-meta {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 60px;
}

.sentiment-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.positive {
  color: green;
}

.negative {
  color: red;
}

.neutral {
  color: gray;
}

.author {
  font-size: 12px;
  color: #555;
}
</style>
