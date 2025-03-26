<template>
  <div>
    <h2 id="page-heading" data-cy="FilterHeading">
      <span v-text="t$('pampaInsightsApp.filter.home.title')" id="filter-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('pampaInsightsApp.filter.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'FilterCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-filter"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('pampaInsightsApp.filter.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && filters && filters.length === 0">
      <span v-text="t$('pampaInsightsApp.filter.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="filters && filters.length > 0">
      <table class="table table-striped" aria-describedby="filters">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.filter.name')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.filter.visualization')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.filter.typeOfChart')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.filter.emotions')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.filter.search')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="filter in filters" :key="filter.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'FilterView', params: { filterId: filter.id } }">{{ filter.id }}</router-link>
            </td>
            <td>{{ filter.name }}</td>
            <td v-text="t$('pampaInsightsApp.Visualization.' + filter.visualization)"></td>
            <td v-text="t$('pampaInsightsApp.TypeOfChart.' + filter.typeOfChart)"></td>
            <td v-text="t$('pampaInsightsApp.Emotions.' + filter.emotions)"></td>
            <td>
              <div v-if="filter.search">
                <router-link :to="{ name: 'SearchView', params: { searchId: filter.search.id } }">{{ filter.search.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'FilterView', params: { filterId: filter.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'FilterEdit', params: { filterId: filter.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(filter)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="pampaInsightsApp.filter.delete.question" data-cy="filterDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-filter-heading" v-text="t$('pampaInsightsApp.filter.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-filter"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeFilter()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./filter.component.ts"></script>
