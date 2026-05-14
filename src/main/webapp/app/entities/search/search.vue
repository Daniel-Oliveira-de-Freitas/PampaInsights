<template>
  <div>
    <div id="page-heading" data-cy="SearchHeading" class="d-flex justify-content-between align-items-center">
      <h2 id="search-heading" v-text="t$('pampaInsightsApp.search.home.title')"></h2>
      <div class="d-flex align-items-center gap-2">
        <select v-model="selectedSort" @change="handleSortChange" class="form-select mr-1" style="width: 130px; height: 37px">
          <option value="all">Todas</option>
          <option value="favorite">Favoritas</option>
        </select>
        <button class="btn btn-info mr-1" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('pampaInsightsApp.search.home.refreshListLabel')"></span>
        </button>
        <button id="jh-create-entity" class="btn btn-primary" @click="openCreateModal">
          <font-awesome-icon icon="plus" />
          <span v-text="t$('pampaInsightsApp.search.home.createLabel')"></span>
        </button>
      </div>
    </div>

    <br />
    <div class="alert alert-warning" v-if="!isFetching && searches.length === 0">
      <span v-text="t$('pampaInsightsApp.search.home.notFound')"></span>
    </div>

    <div class="row" v-if="searches.length > 0">
      <div class="col-md-3" v-for="search in searches" :key="search.id">
        <div class="card search-card mt-4">
          <div class="card-header d-flex justify-content-between align-items-center">
            <button @click="toggleFavorite(search)" class="btn btn-secondary">
              <font-awesome-icon :icon="['fas', 'star']" :class="{ 'text-warning': search.favorite, 'text-white': !search.favorite }" />
            </button>
            <button @click="prepareRemove(search)" class="btn btn-secondary">
              <font-awesome-icon icon="trash" class="text-danger" />
            </button>
          </div>
          <div class="card-body" @click="goToEdit(search.id)">
            <h5 class="card-title">{{ search.name }}</h5>
            <p class="card-text">{{ formatDateShort(search.createDate) }}</p>
          </div>
        </div>
      </div>
    </div>

    <b-modal ref="createEntity" id="createEntity">
      <template #modal-title>
        <span>Nova Pesquisa</span>
      </template>

      <b-form @submit.prevent="saveSearch">
        <div class="modal-body">
          <b-form-group label="Nome da Pesquisa">
            <b-form-input v-model="search.name" placeholder="Digite o nome da pesquisa"></b-form-input>
          </b-form-group>
        </div>
      </b-form>
      <template #modal-footer>
        <button type="button" class="btn btn-secondary" @click="closeCreateModal">Cancelar</button>
        <button type="submit" class="btn btn-primary" @click="saveSearch">Salvar</button>
      </template>
    </b-modal>

    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p v-text="t$('pampaInsightsApp.search.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <button class="btn btn-secondary" @click="closeDialog">{{ t$('entity.action.cancel') }}</button>
        <button class="btn btn-danger" @click="removeSearch">{{ t$('entity.action.delete') }}</button>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./search.component.ts"></script>

<style scoped>
.search-card {
  cursor: pointer;
  transition: 0.3s;
}
.search-card:hover {
  transform: scale(1.05);
}
</style>
