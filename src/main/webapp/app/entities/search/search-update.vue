<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-md-3">
        <parameter-update v-if="search.id" :searchId="search.id" />
      </div>
      <div class="col-md-6">
        <comment v-if="search.id" :searchId="search.id" @searchComments="$event" />
      </div>
      <div class="col-md-3">
        <filter-update v-if="search.id" :searchId="search.id" @sentimentData="$event" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./search-update.component.ts"></script>
