import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const Search = () => import('@/entities/search/search.vue');
const SearchUpdate = () => import('@/entities/search/search-update.vue');
const SearchDetails = () => import('@/entities/search/search-details.vue');

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
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
