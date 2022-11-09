import VueRouter from "vue-router";

import indexPage from "../components/index-page.vue";
import onlineSearchListPage from "../components/online-search-list.vue";
import onlineChapterPage from "../components/online-chapter.vue";
import onlineReadPage from "../components/online-read.vue";

export default new VueRouter({
  routes: [
    {
      path: "/",
      redirect: "/index",
    },
    {
      path: "/index",
      name: "indexPage",
      component: indexPage,
    },
    {
      path: "/onlineSearch",
      name: "onlineSearchListPage",
      component: onlineSearchListPage,
    },
    {
      path: "/onlineChapter",
      name: "onlineChapterPage",
      component: onlineChapterPage,
    },
    {
      path: "/onlineRead",
      name: "onlineReadPage",
      component: onlineReadPage,
    },
  ],
});
