import { defineComponent, provide } from 'vue';

import CommentService from './comment/comment.service';
import FilterService from './filter/filter.service';
import ParameterService from './parameter/parameter.service';
import SearchService from './search/search.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('commentService', () => new CommentService());
    provide('filterService', () => new FilterService());
    provide('parameterService', () => new ParameterService());
    provide('searchService', () => new SearchService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
