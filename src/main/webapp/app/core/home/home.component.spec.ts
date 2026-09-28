import { shallowMount } from '@vue/test-utils';
import Home from './home.vue';

type HomeComponentType = InstanceType<typeof Home>;

describe('Home', () => {
  let home: HomeComponentType;

  beforeEach(() => {
    const wrapper = shallowMount(Home);
    home = wrapper.vm;
  });

  it('should list the supported sources', () => {
    expect(home.sources).toEqual(['YouTube', 'Facebook', 'Reddit', 'Reclame Aqui', 'Trustpilot']);
  });

  it('should list the three steps of how it works', () => {
    expect(home.steps).toEqual(['create', 'configure', 'analyze']);
  });
});
