import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import FilterDetails from './filter-details.vue';
import FilterService from './filter.service';
import AlertService from '@/shared/alert/alert.service';

type FilterDetailsComponentType = InstanceType<typeof FilterDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const filterSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Filter Management Detail Component', () => {
    let filterServiceStub: SinonStubbedInstance<FilterService>;
    let mountOptions: MountingOptions<FilterDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      filterServiceStub = sinon.createStubInstance<FilterService>(FilterService);

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
          filterService: () => filterServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        filterServiceStub.find.resolves(filterSample);
        route = {
          params: {
            filterId: `${123}`,
          },
        };
        const wrapper = shallowMount(FilterDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.filter).toMatchObject(filterSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        filterServiceStub.find.resolves(filterSample);
        const wrapper = shallowMount(FilterDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
