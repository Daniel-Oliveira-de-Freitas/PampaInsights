<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="comment">
        <h2 class="jh-entity-heading" data-cy="commentDetailsHeading">
          <span v-text="t$('pampaInsightsApp.comment.detail.title')"></span> {{ comment.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('pampaInsightsApp.comment.body')"></span>
          </dt>
          <dd>
            <span>{{ comment.body }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.comment.sentiment')"></span>
          </dt>
          <dd>
            <span>{{ comment.sentiment }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.comment.author')"></span>
          </dt>
          <dd>
            <span>{{ comment.author }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.comment.createDate')"></span>
          </dt>
          <dd>
            <span v-if="comment.createDate">{{ formatDateLong(comment.createDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.comment.search')"></span>
          </dt>
          <dd>
            <div v-if="comment.search">
              <router-link :to="{ name: 'SearchView', params: { searchId: comment.search.id } }">{{ comment.search.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="comment.id" :to="{ name: 'CommentEdit', params: { commentId: comment.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./comment-details.component.ts"></script>
