<template>
  <div>
    <h2 id="page-heading" data-cy="ParameterHeading">
      <span v-text="t$('pampaInsightsApp.parameter.home.title')" id="parameter-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('pampaInsightsApp.parameter.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ParameterCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-parameter"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('pampaInsightsApp.parameter.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && parameters && parameters.length === 0">
      <span v-text="t$('pampaInsightsApp.parameter.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="parameters && parameters.length > 0">
      <table class="table table-striped" aria-describedby="parameters">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.parameter.terms')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.parameter.webSite')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.parameter.instagram')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.parameter.facebook')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.parameter.linkedin')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.parameter.x')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.parameter.createDate')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.parameter.search')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="parameter in parameters" :key="parameter.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ParameterView', params: { parameterId: parameter.id } }">{{ parameter.id }}</router-link>
            </td>
            <td>{{ parameter.terms }}</td>
            <td>{{ parameter.webSite }}</td>
            <td>{{ parameter.instagram }}</td>
            <td>{{ parameter.facebook }}</td>
            <td>{{ parameter.linkedin }}</td>
            <td>{{ parameter.x }}</td>
            <td>{{ formatDateShort(parameter.createDate) || '' }}</td>
            <td>
              <div v-if="parameter.search">
                <router-link :to="{ name: 'SearchView', params: { searchId: parameter.search.id } }">{{ parameter.search.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ParameterView', params: { parameterId: parameter.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ParameterEdit', params: { parameterId: parameter.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(parameter)"
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
        <span
          id="pampaInsightsApp.parameter.delete.question"
          data-cy="parameterDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-parameter-heading" v-text="t$('pampaInsightsApp.parameter.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-parameter"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeParameter()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./parameter.component.ts"></script>
