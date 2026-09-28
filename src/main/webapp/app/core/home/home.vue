<template>
  <div class="home container-fluid p-4">
    <div class="card shadow-sm">
      <div class="card-body p-4 p-lg-5">
        <div class="row align-items-center">
          <div :class="homeResultadosImg ? 'col-lg-6 mb-4 mb-lg-0' : 'col-12'">
            <h1 class="home-title mb-3">
              {{ t$('home.hero.title.p1') }}
              <span class="text-highlight">{{ t$('home.hero.title.p2') }}</span>
            </h1>
            <p class="home-subtitle mb-4" v-text="t$('home.hero.subtitle')"></p>

            <p class="home-sources-label mb-2" v-text="t$('home.sources.label')"></p>
            <div class="home-sources">
              <span v-for="source in sources" :key="source" class="home-source">{{ source }}</span>
            </div>
          </div>

          <div v-if="homeResultadosImg" class="col-lg-6">
            <img :src="homeResultadosImg" :alt="t$('home.hero.imageAlt')" class="home-screenshot img-fluid" />
          </div>
        </div>

        <hr class="my-5" />

        <h2 class="home-section-title mb-4" v-text="t$('home.steps.title')"></h2>
        <div class="row">
          <div v-for="(step, index) in steps" :key="step" class="col-md-4 mb-3">
            <div class="home-step h-100">
              <span class="home-step-number">{{ index + 1 }}</span>
              <h3 class="home-step-title" v-text="t$(`home.steps.${step}.title`)"></h3>
              <p class="mb-0" v-text="t$(`home.steps.${step}.text`)"></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>

<style scoped>
.home-title {
  font-size: 2.4rem;
  font-weight: 600;
  line-height: 1.2;
}

.home-subtitle {
  font-size: 1.15rem;
  color: #555;
}

.home-sources-label {
  font-size: 0.9rem;
  color: #666;
}

.home-sources {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.home-source {
  padding: 0.3rem 0.8rem;
  border: 1px solid #cfd8d2;
  border-radius: 999px;
  background-color: #f3f7f4;
  font-size: 0.9rem;
}

.home-screenshot {
  width: 100%;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.home-section-title {
  font-size: 1.6rem;
  font-weight: 600;
}

.home-step {
  padding: 1.25rem;
  border: 1px solid #dee2e6;
  border-radius: 8px;
}

.home-step-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  margin-bottom: 0.75rem;
  border-radius: 50%;
  background-color: #135526;
  color: #fff;
  font-weight: 600;
}

.home-step-title {
  font-size: 1.15rem;
  font-weight: 600;
}

/* Tema escuro */
:global(body.theme-dark) .home-subtitle,
:global(body.theme-dark) .home-sources-label {
  color: #b8c0bb;
}

:global(body.theme-dark) .home-source {
  border-color: #3d4a42;
  background-color: #2b332e;
  color: #e0e0e0;
}

:global(body.theme-dark) .home-step,
:global(body.theme-dark) .home-screenshot {
  border-color: #444;
}
</style>
