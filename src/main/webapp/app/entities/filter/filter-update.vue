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
          <div class="d-flex align-items-center gap-2 mb-1">
            <label class="form-control-label mb-0" for="filter-sentimentAnalysisType">
              {{ t$('pampaInsightsApp.filter.sentimentAnalysisType') }}
            </label>
            <font-awesome-icon
              id="tooltip-sentimentAnalysisType"
              icon="circle-info"
              class="text-primary"
              style="cursor: pointer; font-size: 13px"
            />
            <b-popover target="tooltip-sentimentAnalysisType" triggers="hover focus" placement="right">
              {{ t$('pampaInsightsApp.filter.tooltip.sentimentAnalysisType') }}
            </b-popover>
          </div>
          <select
            disabled
            class="form-control"
            name="sentimentAnalysisType"
            id="filter-sentimentAnalysisType"
            data-cy="sentimentAnalysisType"
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
          <div class="d-flex align-items-center gap-2 mb-1">
            <label class="form-control-label mb-0" for="filter-visualization">
              {{ t$('pampaInsightsApp.filter.visualization') }}
            </label>
            <font-awesome-icon
              id="tooltip-visualization"
              icon="circle-info"
              class="text-primary"
              style="cursor: pointer; font-size: 13px"
            />
            <b-popover target="tooltip-visualization" triggers="hover focus" placement="right">
              {{ t$('pampaInsightsApp.filter.tooltip.visualization') }}
            </b-popover>
          </div>
          <select
            disabled
            class="form-control"
            name="visualization"
            id="filter-visualization"
            data-cy="visualization"
            v-model="v$.visualization.$model"
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
          <div class="d-flex align-items-center gap-2 mb-1">
            <label class="form-control-label mb-0" for="filter-emotions">
              {{ t$('pampaInsightsApp.filter.emotions') }}
            </label>
            <font-awesome-icon id="tooltip-emotions" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
            <b-popover target="tooltip-emotions" triggers="hover focus" placement="right">
              {{ t$('pampaInsightsApp.filter.tooltip.emotions') }}
            </b-popover>
          </div>
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

        <div class="form-group">
          <div class="d-flex align-items-center gap-2 mb-1">
            <label class="form-control-label mb-0" for="filter-typeOfChart">
              {{ t$('pampaInsightsApp.filter.typeOfChart') }}
            </label>
            <font-awesome-icon id="tooltip-typeOfChart" icon="circle-info" class="text-primary" style="cursor: pointer; font-size: 13px" />
            <b-popover target="tooltip-typeOfChart" triggers="hover focus" placement="right">
              {{ t$('pampaInsightsApp.filter.tooltip.typeOfChart') }}
            </b-popover>
          </div>
          <select
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
          <button type="submit" id="save-entity" data-cy="entityCreateSaveButton" class="btn btn-secondary w-100 mb-4 mt-4">
            <font-awesome-icon icon="search" />
            &nbsp;<span>Aplicar Filtros</span>
          </button>
        </div>
        <div class="mt-4" v-if="showChart">
          <div v-if="appliedTypeOfChart === TypeOfChart.PIZZA && selectedChartData">
            <PieChart :data="selectedChartData" :options="{ responsive: true, maintainAspectRatio: false }" style="height: 300px" />
          </div>

          <div v-else-if="appliedTypeOfChart === TypeOfChart.COLUNAS && selectedChartData">
            <BarChart :data="selectedChartData" :options="columnChartOptions" style="height: 300px" />
          </div>

          <div v-else-if="appliedTypeOfChart === TypeOfChart.BARRAS && selectedChartData">
            <BarChart :data="selectedChartData" :options="barChartOptions" style="height: 300px" />
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
