<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="pampaInsightsApp.comment.home.createOrEditLabel"
          data-cy="CommentCreateUpdateHeading"
          v-text="t$('pampaInsightsApp.comment.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="comment.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="comment.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.comment.body')" for="comment-body"></label>
            <input
              type="text"
              class="form-control"
              name="body"
              id="comment-body"
              data-cy="body"
              :class="{ valid: !v$.body.$invalid, invalid: v$.body.$invalid }"
              v-model="v$.body.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.comment.sentiment')" for="comment-sentiment"></label>
            <input
              type="number"
              class="form-control"
              name="sentiment"
              id="comment-sentiment"
              data-cy="sentiment"
              :class="{ valid: !v$.sentiment.$invalid, invalid: v$.sentiment.$invalid }"
              v-model.number="v$.sentiment.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.comment.author')" for="comment-author"></label>
            <input
              type="text"
              class="form-control"
              name="author"
              id="comment-author"
              data-cy="author"
              :class="{ valid: !v$.author.$invalid, invalid: v$.author.$invalid }"
              v-model="v$.author.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.comment.createDate')" for="comment-createDate"></label>
            <div class="d-flex">
              <input
                id="comment-createDate"
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
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.comment.search')" for="comment-search"></label>
            <select class="form-control" id="comment-search" data-cy="search" name="search" v-model="comment.search">
              <option :value="null"></option>
              <option
                :value="comment.search && searchOption.id === comment.search.id ? comment.search : searchOption"
                v-for="searchOption in searches"
                :key="searchOption.id"
              >
                {{ searchOption.id }}
              </option>
            </select>
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
<script lang="ts" src="./comment-update.component.ts"></script>
