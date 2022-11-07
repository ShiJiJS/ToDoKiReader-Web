<template>
  <div>
    <div class="container">
      <!-- 导航栏 -->
      <nav>
        <ul>
          <li><strong>ToDoKi Reader</strong></li>
        </ul>
        <ul>
          <li><a href="#">Link</a></li>
          <li><a href="#">Link</a></li>
          <li><button @click="toOnlineSearch()">Search</button></li>
        </ul>
      </nav>
      <!-- 搜索框 -->
      <input type="search" v-model="searchContent" />

      <!-- 源表格 -->
      <table>
        <thead>
          <tr>
            <th scope="col">源序号</th>
            <th scope="col">名称</th>
            <th scope="col">是否可用</th>
            <th scope="col">链接</th>
          </tr>
        </thead>
        <tbody>
          <!-- scope="row" -->
          <tr v-for="(source, index) in sources" :key="source.name">
            <th>{{ index }}</th>
            <td>{{ source.name }}</td>
            <td v-if="source.valid == true">是</td>
            <td v-if="source.valid == false">否</td>
            <td>{{ source.url }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import request from "../utils/request";

export default {
  name: "indexPage",
  components: {},
  methods: {
    toOnlineSearch() {
      var _this = this;
      this.$router.push({
        path: "/onlineSearch",
        name: "onlineSearchListPage",
        params: {
          searchContent: _this.searchContent,
          sources: _this.sources,
        },
      });
    },
    getSources() {
      request({
        url: "/api/sources",
        method: "get",
      }).then((response) => {
        this.sources = response.data.data;
      });
    },
  },
  data() {
    return {
      searchContent: "",
      sources: [],
    };
  },
  mounted() {
    this.getSources();
  },
};
</script>

<style></style>
