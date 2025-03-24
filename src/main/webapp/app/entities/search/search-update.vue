<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="pampaInsightsApp.search.home.createOrEditLabel"
          data-cy="SearchCreateUpdateHeading"
          v-text="t$('pampaInsightsApp.search.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="search.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="search.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.search.name')" for="search-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="search-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.search.favorite')" for="search-favorite"></label>
            <input
              type="checkbox"
              class="form-check"
              name="favorite"
              id="search-favorite"
              data-cy="favorite"
              :class="{ valid: !v$.favorite.$invalid, invalid: v$.favorite.$invalid }"
              v-model="v$.favorite.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.search.createDate')" for="search-createDate"></label>
            <div class="d-flex">
              <input
                id="search-createDate"
                data-cy="createDate"
                type="datetime-local"
                class="form-control"
                name="createDate"
                :class="{ valid: !v$.createDate.$invalid, invalid: v$.createDate.$invalid }"
                :value="convertDateTimeFromServer(v$.createDate.$model)"
                @change="updateInstantField('createDate', $event)"
              />
            </div>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./search-update.component.ts"></script>
