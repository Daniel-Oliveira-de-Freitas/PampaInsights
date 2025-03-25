import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Parameter from './parameter.vue';
import ParameterService from './parameter.service';
import AlertService from '@/shared/alert/alert.service';

type ParameterComponentType = InstanceType<typeof Parameter>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Parameter Management Component', () => {
    let parameterServiceStub: SinonStubbedInstance<ParameterService>;
    let mountOptions: MountingOptions<ParameterComponentType>['global'];

    beforeEach(() => {
      parameterServiceStub = sinon.createStubInstance<ParameterService>(ParameterService);
      parameterServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          parameterService: () => parameterServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        parameterServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Parameter, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(parameterServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.parameters[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ParameterComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Parameter, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        parameterServiceStub.retrieve.reset();
        parameterServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        parameterServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeParameter();
        await comp.$nextTick(); // clear components

        // THEN
        expect(parameterServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(parameterServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
