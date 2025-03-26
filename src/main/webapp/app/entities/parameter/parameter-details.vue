<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="parameter">
        <h2 class="jh-entity-heading" data-cy="parameterDetailsHeading">
          <span v-text="t$('pampaInsightsApp.parameter.detail.title')"></span> {{ parameter.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('pampaInsightsApp.parameter.terms')"></span>
          </dt>
          <dd>
            <span>{{ parameter.terms }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.parameter.webSite')"></span>
          </dt>
          <dd>
            <span>{{ parameter.webSite }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.parameter.instagram')"></span>
          </dt>
          <dd>
            <span>{{ parameter.instagram }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.parameter.facebook')"></span>
          </dt>
          <dd>
            <span>{{ parameter.facebook }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.parameter.linkedin')"></span>
          </dt>
          <dd>
            <span>{{ parameter.linkedin }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.parameter.x')"></span>
          </dt>
          <dd>
            <span>{{ parameter.x }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.parameter.createDate')"></span>
          </dt>
          <dd>
            <span v-if="parameter.createDate">{{ formatDateLong(parameter.createDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('pampaInsightsApp.parameter.search')"></span>
          </dt>
          <dd>
            <div v-if="parameter.search">
              <router-link :to="{ name: 'SearchView', params: { searchId: parameter.search.id } }">{{ parameter.search.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="parameter.id"
          :to="{ name: 'ParameterEdit', params: { parameterId: parameter.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./parameter-details.component.ts"></script>
