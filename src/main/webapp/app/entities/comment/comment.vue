<template>
  <div>
    <h2 id="page-heading" data-cy="CommentHeading">
      <span v-text="t$('pampaInsightsApp.comment.home.title')" id="comment-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('pampaInsightsApp.comment.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CommentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-comment"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('pampaInsightsApp.comment.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && comments && comments.length === 0">
      <span v-text="t$('pampaInsightsApp.comment.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="comments && comments.length > 0">
      <table class="table table-striped" aria-describedby="comments">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.comment.body')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.comment.sentiment')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.comment.author')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.comment.createDate')"></span></th>
            <th scope="row"><span v-text="t$('pampaInsightsApp.comment.search')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="comment in comments" :key="comment.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CommentView', params: { commentId: comment.id } }">{{ comment.id }}</router-link>
            </td>
            <td>{{ comment.body }}</td>
            <td>{{ comment.sentiment }}</td>
            <td>{{ comment.author }}</td>
            <td>{{ formatDateShort(comment.createDate) || '' }}</td>
            <td>
              <div v-if="comment.search">
                <router-link :to="{ name: 'SearchView', params: { searchId: comment.search.id } }">{{ comment.search.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CommentView', params: { commentId: comment.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CommentEdit', params: { commentId: comment.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(comment)"
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
        <span id="pampaInsightsApp.comment.delete.question" data-cy="commentDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-comment-heading" v-text="t$('pampaInsightsApp.comment.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-comment"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeComment()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./comment.component.ts"></script>
