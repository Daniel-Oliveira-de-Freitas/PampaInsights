<template>
  <div></div>
  <div class="row justify-content-center">
    <div class="col-10">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="pampaInsightsApp.parameter.home.createOrEditLabel"
          data-cy="ParameterCreateUpdateHeading"
          v-text="t$('pampaInsightsApp.parameter.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group">
            <div class="d-flex align-items-center gap-2 mb-1">
              <label class="form-control-label mb-0" for="parameter-terms">
                {{ t$('pampaInsightsApp.parameter.terms') }}
              </label>
              <font-awesome-icon id="tooltip-terms" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
              <b-popover target="tooltip-terms" triggers="hover focus" placement="right">
                {{ t$('pampaInsightsApp.parameter.tooltip.terms') }}
              </b-popover>
            </div>
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
            <div class="d-flex align-items-center gap-2 mb-1">
              <label class="form-control-label mb-0" for="parameter-webSite">
                {{ t$('pampaInsightsApp.parameter.webSite') }}
              </label>
              <font-awesome-icon id="tooltip-webSite" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
              <b-popover target="tooltip-webSite" triggers="hover focus" placement="right">
                {{ t$('pampaInsightsApp.parameter.tooltip.webSite') }}
              </b-popover>
            </div>
            <input
              type="text"
              class="form-control"
              name="webSite"
              id="parameter-webSite"
              data-cy="webSite"
              v-model="v$.webSite.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <div class="d-flex align-items-center gap-2 mb-1">
              <label class="form-control-label mb-0" for="parameter-maxPages">
                {{ t$('pampaInsightsApp.parameter.maxPages') }}
              </label>
              <font-awesome-icon id="tooltip-maxPages" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
              <b-popover target="tooltip-maxPages" triggers="hover focus" placement="right">
                {{ t$('pampaInsightsApp.parameter.tooltip.maxPages') }}
              </b-popover>
            </div>
            <input
              @input="validateMaxPages"
              type="number"
              class="form-control"
              name="maxPages"
              id="parameter-maxPages"
              v-model.number="maxPages"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <div class="d-flex align-items-center gap-2 mb-1">
              <label class="form-control-label mb-0" for="parameter-instagram">
                {{ t$('pampaInsightsApp.parameter.instagram') }}
              </label>
              <font-awesome-icon id="tooltip-instagram" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
              <b-popover target="tooltip-instagram" triggers="hover focus" placement="right">
                {{ t$('pampaInsightsApp.parameter.tooltip.webSiteUrl') }}
              </b-popover>
            </div>
            <input
              type="text"
              class="form-control"
              name="instagram"
              id="parameter-instagram"
              data-cy="instagram"
              v-model="v$.instagram.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <div class="d-flex align-items-center gap-2 mb-1">
              <label class="form-control-label mb-0" for="parameter-facebook">
                {{ t$('pampaInsightsApp.parameter.facebook') }}
              </label>
              <font-awesome-icon id="tooltip-facebook" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
              <b-popover target="tooltip-facebook" triggers="hover focus" placement="right">
                {{ t$('pampaInsightsApp.parameter.tooltip.webSiteUrl') }}
              </b-popover>
            </div>
            <input
              type="text"
              class="form-control"
              name="facebook"
              id="parameter-facebook"
              data-cy="facebook"
              v-model="v$.facebook.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <div class="d-flex align-items-center gap-2 mb-1">
              <label class="form-control-label mb-0" for="parameter-linkedin">
                {{ t$('pampaInsightsApp.parameter.linkedin') }}
              </label>
              <font-awesome-icon id="tooltip-linkedin" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
              <b-popover target="tooltip-linkedin" triggers="hover focus" placement="right">
                {{ t$('pampaInsightsApp.parameter.tooltip.webSiteUrl') }}
              </b-popover>
            </div>
            <input
              type="text"
              class="form-control"
              name="linkedin"
              id="parameter-linkedin"
              data-cy="linkedin"
              v-model="v$.linkedin.$model"
              :readonly="!isEditing"
            />
          </div>
          <div class="form-group">
            <div class="d-flex align-items-center gap-2 mb-1">
              <label class="form-control-label mb-0" for="parameter-x">
                {{ t$('pampaInsightsApp.parameter.x') }}
              </label>
              <font-awesome-icon id="tooltip-x" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
              <b-popover target="tooltip-x" triggers="hover focus" placement="right">
                {{ t$('pampaInsightsApp.parameter.tooltip.webSiteUrl') }}
              </b-popover>
            </div>
            <input type="text" class="form-control" name="x" id="parameter-x" data-cy="x" v-model="v$.x.$model" :readonly="!isEditing" />
          </div>
        </div>
        <div class="d-flex flex-column">
          <div class="d-flex justify-content-between mb-2">
            <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
              <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
            </button>
            <div>
              <button v-if="!isEditing" type="button" id="edit-save" class="btn btn-info me-2" @click="toggleEdit()">
                <font-awesome-icon icon="edit"></font-awesome-icon>&nbsp;<span>Editar</span>
              </button>
              <button
                v-if="isEditing"
                type="submit"
                id="save-entity"
                data-cy="entityCreateSaveButton"
                :disabled="v$.$invalid || isSaving"
                class="btn btn-primary"
              >
                <font-awesome-icon icon="save" />
                &nbsp;<span>Salvar</span>
              </button>
            </div>
          </div>
          <button :disabled="isEditing" type="button" class="btn btn-secondary w-100 mb-4 mt-4" @click="searchComments()">
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
.invalid {
  border-color: black;
}
</style>
