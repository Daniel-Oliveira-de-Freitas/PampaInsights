import { defineComponent } from 'vue';
import { useI18n } from 'vue-i18n';

// Print da tela de resultados. Carregado só se o arquivo existir, para a home não quebrar sem ele.
const homeImages = import.meta.glob<string>('../../../content/images/home_resultados.png', { eager: true, import: 'default' });
const homeResultadosImg = Object.values(homeImages)[0] ?? null;

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const sources = ['YouTube', 'Facebook', 'Reddit', 'Reclame Aqui', 'Trustpilot'];
    const steps = ['create', 'configure', 'analyze'];

    return {
      t$: useI18n().t,
      sources,
      steps,
      homeResultadosImg,
    };
  },
});
