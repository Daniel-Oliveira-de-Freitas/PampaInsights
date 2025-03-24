import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Search from './search.vue';
import SearchService from './search.service';
import AlertService from '@/shared/alert/alert.service';

type SearchComponentType = InstanceType<typeof Search>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Search Management Component', () => {
    let searchServiceStub: SinonStubbedInstance<SearchService>;
    let mountOptions: MountingOptions<SearchComponentType>['global'];

    beforeEach(() => {
      searchServiceStub = sinon.createStubInstance<SearchService>(SearchService);
      searchServiceStub.retrieve.resolves({ headers: {} });

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
          searchService: () => searchServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        searchServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Search, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(searchServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.searches[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: SearchComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Search, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        searchServiceStub.retrieve.reset();
        searchServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        searchServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeSearch();
        await comp.$nextTick(); // clear components

        // THEN
        expect(searchServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(searchServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
