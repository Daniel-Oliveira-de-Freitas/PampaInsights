import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const Search = () => import('@/entities/search/search.vue');
const SearchUpdate = () => import('@/entities/search/search-update.vue');
const SearchDetails = () => import('@/entities/search/search-details.vue');

const Comment = () => import('@/entities/comment/comment.vue');
const CommentUpdate = () => import('@/entities/comment/comment-update.vue');
const CommentDetails = () => import('@/entities/comment/comment-details.vue');

const Filter = () => import('@/entities/filter/filter.vue');
const FilterUpdate = () => import('@/entities/filter/filter-update.vue');
const FilterDetails = () => import('@/entities/filter/filter-details.vue');

const Parameter = () => import('@/entities/parameter/parameter.vue');
const ParameterUpdate = () => import('@/entities/parameter/parameter-update.vue');
const ParameterDetails = () => import('@/entities/parameter/parameter-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'search',
      name: 'Search',
      component: Search,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'search/new',
      name: 'SearchCreate',
      component: SearchUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'search/:searchId/edit',
      name: 'SearchEdit',
      component: SearchUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'search/:searchId/view',
      name: 'SearchView',
      component: SearchDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'comment',
      name: 'Comment',
      component: Comment,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'comment/new',
      name: 'CommentCreate',
      component: CommentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'comment/:commentId/edit',
      name: 'CommentEdit',
      component: CommentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'comment/:commentId/view',
      name: 'CommentView',
      component: CommentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'filter',
      name: 'Filter',
      component: Filter,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'filter/new',
      name: 'FilterCreate',
      component: FilterUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'filter/:filterId/edit',
      name: 'FilterEdit',
      component: FilterUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'filter/:filterId/view',
      name: 'FilterView',
      component: FilterDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'parameter',
      name: 'Parameter',
      component: Parameter,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'parameter/new',
      name: 'ParameterCreate',
      component: ParameterUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'parameter/:parameterId/edit',
      name: 'ParameterEdit',
      component: ParameterUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'parameter/:parameterId/view',
      name: 'ParameterView',
      component: ParameterDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
