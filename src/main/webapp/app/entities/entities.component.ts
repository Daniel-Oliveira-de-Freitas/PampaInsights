import { defineComponent, provide } from 'vue';

import SearchService from './search/search.service';
import CommentService from './comment/comment.service';
import FilterService from './filter/filter.service';
import ParameterService from './parameter/parameter.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('searchService', () => new SearchService());
    provide('commentService', () => new CommentService());
    provide('filterService', () => new FilterService());
    provide('parameterService', () => new ParameterService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
