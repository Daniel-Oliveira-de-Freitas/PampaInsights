<template>
  <div class="row justify-content-center mt-05">
    <div class="col-10">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="pampaInsightsApp.filter.home.createOrEditLabel"
          data-cy="FilterCreateUpdateHeading"
          v-text="t$('pampaInsightsApp.filter.home.createOrEditLabel')"
          class="mb-4"
        ></h2>
        <div class="form-group">
          <label
            class="form-control-label"
            v-text="t$('pampaInsightsApp.filter.sentimentAnalysisType')"
            for="filter-sentimentAnalysisType"
          ></label>
          <select
            :disabled="!isEditing"
            class="form-control"
            name="sentimentAnalysisType"
            id="filter-sentimentAnalysisType"
            data-cy="sentimentAnalysisType"
            :class="{ valid: !v$.sentimentAnalysisType.$invalid, invalid: v$.sentimentAnalysisType.$invalid }"
            v-model="v$.sentimentAnalysisType.$model"
          >
            <option
              v-for="sentimentAnalysisType in sentimentAnalysisTypeValues"
              :key="sentimentAnalysisType"
              :value="sentimentAnalysisType"
              :label="t$('pampaInsightsApp.sentimentAnalysisType.' + sentimentAnalysisType)"
            >
              {{ sentimentAnalysisType }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-control-label" v-text="t$('pampaInsightsApp.filter.emotions')" for="filter-emotions"></label>
          <select
            :disabled="!isEditing"
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
        <div class="form-group">
          <label class="form-control-label" v-text="t$('pampaInsightsApp.filter.visualization')" for="filter-visualization"></label>
          <select
            :disabled="!isEditing"
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
            :disabled="!isEditing"
            class="form-control"
            name="typeOfChart"
            :class="{ valid: !v$.typeOfChart.$invalid, invalid: v$.typeOfChart.$invalid }"
            v-model="v$.typeOfChart.$model"
            id="filter-typeOfChart"
            data-cy="typeOfChart"
          >
            <option :value="null"></option>
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

          <button :disabled="isEditing" type="button" class="btn btn-secondary w-100 mb-4 mt-4" @click="applyFilters">
            <font-awesome-icon icon="search" />
            &nbsp;<span>Aplicar Filtros</span>
          </button>
        </div>

        <div class="mt-4" v-if="showChart && !isEditing">
          <div v-if="v$.typeOfChart.$model === TypeOfChart.PIZZA && selectedChartData">
            <PieChart :data="selectedChartData" :options="{ responsive: true, maintainAspectRatio: false }" style="height: 300px" />
          </div>

          <div v-else-if="v$.typeOfChart.$model === TypeOfChart.BARRAS && selectedChartData">
            <BarChart :data="selectedChartData" :options="{ responsive: true, maintainAspectRatio: false }" style="height: 300px" />
          </div>

          <div v-else-if="v$.typeOfChart.$model === TypeOfChart.COLUNAS && selectedChartData">
            <BarChart
              :data="selectedChartData"
              :options="{ indexAxis: 'y', responsive: true, maintainAspectRatio: false }"
              style="height: 300px"
            />
          </div>

          <div v-else class="text-center text-muted">Nenhum gráfico selecionado.</div>
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

.hr {
  border-color: white;
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
