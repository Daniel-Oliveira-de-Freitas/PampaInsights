<template>
  <div class="flex-grid">
    <div class="chat-container">
      <akip-chat />
    </div>
  </div>
</template>
<script src="./akip-chat-page.component.ts"></script>

<style scoped>
.flex-grid {
  display: flex;
  height: 100vh;
  width: 100%;
}

.chat-container {
  flex: 1;
  display: flex;
  justify-content: start;
  align-items: center;
}

.model-selector-container {
  flex: 1;
  display: flex;
  justify-content: end;
  align-items: center;
}
</style>
