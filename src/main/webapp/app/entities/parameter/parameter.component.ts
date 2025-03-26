import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import ParameterService from './parameter.service';
import { type IParameter } from '@/shared/model/parameter.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Parameter',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const parameterService = inject('parameterService', () => new ParameterService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const parameters: Ref<IParameter[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveParameters = async () => {
      isFetching.value = true;
      try {
        const res = await parameterService().retrieve();
        parameters.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveParameters();
    };

    onMounted(async () => {
      await retrieveParameters();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IParameter) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeParameter = async () => {
      try {
        await parameterService().delete(removeId.value);
        const message = t$('pampaInsightsApp.parameter.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveParameters();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      parameters,
      handleSyncList,
      isFetching,
      retrieveParameters,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeParameter,
      t$,
    };
  },
});
