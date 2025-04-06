<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="pampaInsightsApp.filter.home.createOrEditLabel"
          data-cy="FilterCreateUpdateHeading"
          v-text="t$('pampaInsightsApp.filter.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="filter.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="filter.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.filter.name')" for="filter-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="filter-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.filter.visualization')" for="filter-visualization"></label>
            <select
              class="form-control"
              name="visualization"
              :class="{ valid: !v$.visualization.$invalid, invalid: v$.visualization.$invalid }"
              v-model="v$.visualization.$model"
              id="filter-visualization"
              data-cy="visualization"
            >
              <option
                v-for="visualization in visualizationValues"
                :key="visualization"
                :value="visualization"
                :label="t$('pampaInsightsApp.Visualization.' + visualization)"
              >
                {{ visualization }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.filter.typeOfChart')" for="filter-typeOfChart"></label>
            <select
              class="form-control"
              name="typeOfChart"
              :class="{ valid: !v$.typeOfChart.$invalid, invalid: v$.typeOfChart.$invalid }"
              v-model="v$.typeOfChart.$model"
              id="filter-typeOfChart"
              data-cy="typeOfChart"
            >
              <option
                v-for="typeOfChart in typeOfChartValues"
                :key="typeOfChart"
                :value="typeOfChart"
                :label="t$('pampaInsightsApp.TypeOfChart.' + typeOfChart)"
              >
                {{ typeOfChart }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('pampaInsightsApp.filter.emotions')" for="filter-emotions"></label>
            <select
              class="form-control"
              name="emotions"
              :class="{ valid: !v$.emotions.$invalid, invalid: v$.emotions.$invalid }"
              v-model="v$.emotions.$model"
              id="filter-emotions"
              data-cy="emotions"
            >
              <option
                v-for="emotions in emotionsValues"
                :key="emotions"
                :value="emotions"
                :label="t$('pampaInsightsApp.Emotions.' + emotions)"
              >
                {{ emotions }}
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

<script lang="ts" src="./filter-update.component.ts"></script>

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
  right: 0;
  width: 400px;
  height: 100%;
  background: rgba(45, 42, 42, 0.9);
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.3);
  padding: 20px;
  z-index: 999;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.sidebar-content {
  flex-grow: 1;
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
