<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="pampaInsightsApp.parameter.home.createOrEditLabel"
          data-cy="ParameterCreateUpdateHeading"
          v-text="t$('pampaInsightsApp.parameter.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="parameter.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="parameter.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.parameter.terms')" for="parameter-terms"></label>
            <input
              type="text"
              class="form-control"
              name="terms"
              id="parameter-terms"
              data-cy="terms"
              :class="{ valid: !v$.terms.$invalid, invalid: v$.terms.$invalid }"
              v-model="v$.terms.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.parameter.webSite')" for="parameter-webSite"></label>
            <input
              type="text"
              class="form-control"
              name="webSite"
              id="parameter-webSite"
              data-cy="webSite"
              :class="{ valid: !v$.webSite.$invalid, invalid: v$.webSite.$invalid }"
              v-model="v$.webSite.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.parameter.instagram')" for="parameter-instagram"></label>
            <input
              type="text"
              class="form-control"
              name="instagram"
              id="parameter-instagram"
              data-cy="instagram"
              :class="{ valid: !v$.instagram.$invalid, invalid: v$.instagram.$invalid }"
              v-model="v$.instagram.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.parameter.facebook')" for="parameter-facebook"></label>
            <input
              type="text"
              class="form-control"
              name="facebook"
              id="parameter-facebook"
              data-cy="facebook"
              :class="{ valid: !v$.facebook.$invalid, invalid: v$.facebook.$invalid }"
              v-model="v$.facebook.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.parameter.linkedin')" for="parameter-linkedin"></label>
            <input
              type="text"
              class="form-control"
              name="linkedin"
              id="parameter-linkedin"
              data-cy="linkedin"
              :class="{ valid: !v$.linkedin.$invalid, invalid: v$.linkedin.$invalid }"
              v-model="v$.linkedin.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.parameter.x')" for="parameter-x"></label>
            <input
              type="text"
              class="form-control"
              name="x"
              id="parameter-x"
              data-cy="x"
              :class="{ valid: !v$.x.$invalid, invalid: v$.x.$invalid }"
              v-model="v$.x.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.parameter.createDate')" for="parameter-createDate"></label>
            <div class="d-flex">
              <input
                id="parameter-createDate"
                data-cy="createDate"
                type="datetime-local"
                class="form-control"
                name="createDate"
                :class="{ valid: !v$.createDate.$invalid, invalid: v$.createDate.$invalid }"
                :value="convertDateTimeFromServer(v$.createDate.$model)"
                @change="updateInstantField('createDate', $event)"
                :readonly="!isEditing"
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
            <font-awesome-icon :icon="isEditing ? 'save' : 'edit'" />
            &nbsp;<span v-text="isEditing ? 'Salvar' : 'Editar'"></span>
          </button>
          <button type="button" class="btn btn-secondary" @click="searchComments()">
            <font-awesome-icon icon="search" />
            &nbsp;<span>Buscar Comentários</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./parameter-update.component.ts"></script>
<style scoped>
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 400px;
  height: 100%;
  background: rgba(45, 42, 42, 0.3);
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.3);
  padding: 20px;
  z-index: 999;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.sidebar-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.invalid {
  border-color: red;
}
</style>
