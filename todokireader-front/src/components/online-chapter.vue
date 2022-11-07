<template>
  <div>
    <div class="container">
      <!-- 上部标签 -->
      <article class="info-cart">
        <div class="cover">
          <img :src="menuInfo.coverUrl" alt="" />
        </div>
        <div class="info">
          <table>
            <thead>
              <tr>
                <th scope="col">{{ menuInfo.title }}</th>
              </tr>
            </thead>
          </table>
          <table role="grid">
            <tbody>
              <tr>
                <th scope="row">作者</th>
                <td>{{ menuInfo.author }}</td>
              </tr>
              <tr>
                <th scope="row">发行状态</th>
                <td>{{ menuInfo.status }}</td>
              </tr>
              <tr>
                <th scope="row">最近更新</th>
                <td>{{ menuInfo.lastUpdateTime }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <!-- 简介 -->
      <details>
        <summary role="button">漫画简介</summary>
        <p>{{ menuInfo.synopsis }}</p>
      </details>

      <!-- 章节选择 -->
      <article class="chapter-select">
        <h4>章节选择</h4>
        <div class="chapter-cart">
          <a
            :href="value"
            role="button"
            class="secondary outline"
            v-for="(value, key, index) in menuInfo.chapters"
            :key="index">
            {{ key }}
          </a>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
import request from "../utils/request";
export default {
  name: "onlineChapter",
  methods: {
    getMenuInfo() {
      var data = { url: this.url, sourceName: this.sourceName };
      var _this = this;
      request({
        url: "/api/menu",
        method: "post",
        headers: {
          "Content-Type": "application/json",
        },
        data: JSON.stringify(data),
      }).then((response) => {
        _this.menuInfo = response.data.data;
      });
    },
  },
  data() {
    return {
      url: "",
      sourceName: "",
      menuInfo: [],
    };
  },
  created() {
    this.url = this.$route.params.url;
    this.sourceName = this.$route.params.sourceName;
  },
  mounted() {
    this.getMenuInfo();
  },
};
</script>

<style>
.info-cart {
  height: 400px;
}
.cover {
  height: 100%;
  width: 30%;
  float: left;
}
.info {
  height: 100%;
  width: 70%;

  float: left;
}
img {
  height: 100%;
}
.chapter-cart a {
  width: 230px;
  margin-right: 10px;
  margin-bottom: 10px;
}
.chapter-cart {
  margin-left: 35px;
}
</style>
