import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import FilterUpdate from './filter-update.vue';
import FilterService from './filter.service';
import AlertService from '@/shared/alert/alert.service';

import SearchService from '@/entities/search/search.service';

type FilterUpdateComponentType = InstanceType<typeof FilterUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const filterSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<FilterUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Filter Management Update Component', () => {
    let comp: FilterUpdateComponentType;
    let filterServiceStub: SinonStubbedInstance<FilterService>;

    beforeEach(() => {
      route = {};
      filterServiceStub = sinon.createStubInstance<FilterService>(FilterService);
      filterServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          filterService: () => filterServiceStub,
          searchService: () =>
            sinon.createStubInstance<SearchService>(SearchService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(FilterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.filter = filterSample;
        filterServiceStub.update.resolves(filterSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(filterServiceStub.update.calledWith(filterSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        filterServiceStub.create.resolves(entity);
        const wrapper = shallowMount(FilterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.filter = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(filterServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        filterServiceStub.find.resolves(filterSample);
        filterServiceStub.retrieve.resolves([filterSample]);

        // WHEN
        route = {
          params: {
            filterId: `${filterSample.id}`,
          },
        };
        const wrapper = shallowMount(FilterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.filter).toMatchObject(filterSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        filterServiceStub.find.resolves(filterSample);
        const wrapper = shallowMount(FilterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
