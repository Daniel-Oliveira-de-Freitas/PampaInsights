import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ParameterUpdate from './parameter-update.vue';
import ParameterService from './parameter.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import SearchService from '@/entities/search/search.service';

type ParameterUpdateComponentType = InstanceType<typeof ParameterUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const parameterSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ParameterUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Parameter Management Update Component', () => {
    let comp: ParameterUpdateComponentType;
    let parameterServiceStub: SinonStubbedInstance<ParameterService>;

    beforeEach(() => {
      route = {};
      parameterServiceStub = sinon.createStubInstance<ParameterService>(ParameterService);
      parameterServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          parameterService: () => parameterServiceStub,
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

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ParameterUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ParameterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.parameter = parameterSample;
        parameterServiceStub.update.resolves(parameterSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(parameterServiceStub.update.calledWith(parameterSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        parameterServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ParameterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.parameter = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(parameterServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        parameterServiceStub.find.resolves(parameterSample);
        parameterServiceStub.retrieve.resolves([parameterSample]);

        // WHEN
        route = {
          params: {
            parameterId: `${parameterSample.id}`,
          },
        };
        const wrapper = shallowMount(ParameterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.parameter).toMatchObject(parameterSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        parameterServiceStub.find.resolves(parameterSample);
        const wrapper = shallowMount(ParameterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
