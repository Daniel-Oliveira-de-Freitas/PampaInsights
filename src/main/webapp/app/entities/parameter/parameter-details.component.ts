import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ParameterService from './parameter.service';
import { useDateFormat } from '@/shared/composables';
import { type IParameter } from '@/shared/model/parameter.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ParameterDetails',
  setup() {
    const dateFormat = useDateFormat();
    const parameterService = inject('parameterService', () => new ParameterService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const parameter: Ref<IParameter> = ref({});

    const retrieveParameter = async parameterId => {
      try {
        const res = await parameterService().find(parameterId);
        parameter.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.parameterId) {
      retrieveParameter(route.params.parameterId);
    }

    return {
      ...dateFormat,
      alertService,
      parameter,

      previousState,
      t$: useI18n().t,
    };
  },
});
