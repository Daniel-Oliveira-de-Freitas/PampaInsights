import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import SearchDetails from './search-details.vue';
import SearchService from './search.service';
import AlertService from '@/shared/alert/alert.service';

type SearchDetailsComponentType = InstanceType<typeof SearchDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const searchSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Search Management Detail Component', () => {
    let searchServiceStub: SinonStubbedInstance<SearchService>;
    let mountOptions: MountingOptions<SearchDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      searchServiceStub = sinon.createStubInstance<SearchService>(SearchService);

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
          searchService: () => searchServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        searchServiceStub.find.resolves(searchSample);
        route = {
          params: {
            searchId: `${123}`,
          },
        };
        const wrapper = shallowMount(SearchDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.search).toMatchObject(searchSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        searchServiceStub.find.resolves(searchSample);
        const wrapper = shallowMount(SearchDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
