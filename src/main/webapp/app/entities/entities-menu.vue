<template>
  <div>
    <b-dropdown-item to="/comment">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.comment')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/filter">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.filter')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/parameter">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.parameter')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/search">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.search')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
