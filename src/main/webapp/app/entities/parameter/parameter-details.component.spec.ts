import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ParameterDetails from './parameter-details.vue';
import ParameterService from './parameter.service';
import AlertService from '@/shared/alert/alert.service';

type ParameterDetailsComponentType = InstanceType<typeof ParameterDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const parameterSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Parameter Management Detail Component', () => {
    let parameterServiceStub: SinonStubbedInstance<ParameterService>;
    let mountOptions: MountingOptions<ParameterDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      parameterServiceStub = sinon.createStubInstance<ParameterService>(ParameterService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          parameterService: () => parameterServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        parameterServiceStub.find.resolves(parameterSample);
        route = {
          params: {
            parameterId: `${123}`,
          },
        };
        const wrapper = shallowMount(ParameterDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.parameter).toMatchObject(parameterSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        parameterServiceStub.find.resolves(parameterSample);
        const wrapper = shallowMount(ParameterDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
