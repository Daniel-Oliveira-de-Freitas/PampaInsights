<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="filter">
        <h2 class="jh-entity-heading" data-cy="filterDetailsHeading">
          <span v-text="t$('pampaInsightsApp.filter.detail.title')"></span> {{ filter.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('pampaInsightsApp.filter.name')"></span>
          </dt>
          <dd>
            <span>{{ filter.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.filter.visualization')"></span>
          </dt>
          <dd>
            <span v-text="t$('pampaInsightsApp.Visualization.' + filter.visualization)"></span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.filter.typeOfChart')"></span>
          </dt>
          <dd>
            <span v-text="t$('pampaInsightsApp.TypeOfChart.' + filter.typeOfChart)"></span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.filter.emotions')"></span>
          </dt>
          <dd>
            <span v-text="t$('pampaInsightsApp.Emotions.' + filter.emotions)"></span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.filter.search')"></span>
          </dt>
          <dd>
            <div v-if="filter.search">
              <router-link :to="{ name: 'SearchView', params: { searchId: filter.search.id } }">{{ filter.search.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="filter.id" :to="{ name: 'FilterEdit', params: { filterId: filter.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./filter-details.component.ts"></script>
