<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="search">
        <h2 class="jh-entity-heading" data-cy="searchDetailsHeading">
          <span v-text="t$('pampaInsightsApp.search.detail.title')"></span> {{ search.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('pampaInsightsApp.search.name')"></span>
          </dt>
          <dd>
            <span>{{ search.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.search.favorite')"></span>
          </dt>
          <dd>
            <span>{{ search.favorite }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.search.createDate')"></span>
          </dt>
          <dd>
            <span v-if="search.createDate">{{ formatDateLong(search.createDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.search.user')"></span>
          </dt>
          <dd>
            {{ search.user ? search.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="search.id" :to="{ name: 'SearchEdit', params: { searchId: search.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./search-details.component.ts"></script>
