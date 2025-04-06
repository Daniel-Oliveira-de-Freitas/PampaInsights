import { type ComputedRef, defineComponent, inject } from 'vue';
import { useI18n } from 'vue-i18n';

import type LoginService from '@/account/login.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const loginService = inject<LoginService>('loginService');

    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const slide = 0;
    let sliding = true;

    const openLogin = () => {
      loginService?.openLogin();
    };
    const onSlideStart = (_: number) => {
      sliding = true;
    };
    const onSlideEnd = (_: number) => {
      sliding = false;
    };

    return {
      authenticated,
      openLogin,
      t$: useI18n().t,
      onSlideStart,
      onSlideEnd,
      sliding,
      slide,
    };
  },
});
