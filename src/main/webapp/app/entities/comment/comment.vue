<template>
  <div>
    <h2 id="page-heading" data-cy="CommentHeading">
      <span v-text="t$('pampaInsightsApp.comment.home.title')" id="comment-heading"></span>
    </h2>

    <div class="alert alert-warning" v-if="!isFetching && comments && comments.length === 0">
      <span v-text="t$('pampaInsightsApp.comment.home.notFound')"></span>
    </div>
    <div v-if="comments.length > 0" class="chart-container">
      <PieChart :data="sentimentData" :options="chartOptions" />
    </div>
    <div v-if="comments && comments.length > 0" class="comments-container mt-5 overflow-auto p-2" style="max-height: 70vh">
      <div v-for="comment in comments" :key="comment.id" class="comment-bubble">
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
          <span class="author">{{ comment.author }}</span>
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
  background: #f8f9fa;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
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
