import Vue from "vue";
import App from "./App.vue";
//router
import VueRouter from "vue-router";
import router from "./router";
Vue.use(VueRouter);

import "..\\node_modules\\@picocss\\pico\\css\\pico.min.css";

Vue.config.productionTip = false;

new Vue({
  render: (h) => h(App),
  router: router,
}).$mount("#app");
