import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import SearchUpdate from './search-update.vue';
import SearchService from './search.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type SearchUpdateComponentType = InstanceType<typeof SearchUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const searchSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<SearchUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Search Management Update Component', () => {
    let comp: SearchUpdateComponentType;
    let searchServiceStub: SinonStubbedInstance<SearchService>;

    beforeEach(() => {
      route = {};
      searchServiceStub = sinon.createStubInstance<SearchService>(SearchService);
      searchServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          searchService: () => searchServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(SearchUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(SearchUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.search = searchSample;
        searchServiceStub.update.resolves(searchSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(searchServiceStub.update.calledWith(searchSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        searchServiceStub.create.resolves(entity);
        const wrapper = shallowMount(SearchUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.search = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(searchServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        searchServiceStub.find.resolves(searchSample);
        searchServiceStub.retrieve.resolves([searchSample]);

        // WHEN
        route = {
          params: {
            searchId: `${searchSample.id}`,
          },
        };
        const wrapper = shallowMount(SearchUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.search).toMatchObject(searchSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        searchServiceStub.find.resolves(searchSample);
        const wrapper = shallowMount(SearchUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
