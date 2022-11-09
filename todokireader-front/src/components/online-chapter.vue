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
          <button
            @click="toOnlineRead(value, key)"
            role="button"
            class="secondary outline"
            v-for="(value, key, index) in menuInfo.chapters"
            :key="index">
            {{ key }}
          </button>
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

    toOnlineRead(url, chapter) {
      var _this = this;
      console.log(chapter);
      this.$router.push({
        path: "/onlineRead",
        name: "onlineReadPage",
        params: {
          url: url,
          title: _this.menuInfo.title,
          chapter: chapter,
          sourceName: _this.sourceName,
        },
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
  //判定是否进行了路由切换，如果切换了就重新更新一下页面
  watch: {
    "$route.params.url": {
      handler(newVal) {
        if (newVal != undefined) {
          this.url = this.$route.params.url;
          this.sourceName = this.$route.params.sourceName;
          this.getMenuInfo();
        }
      },
    },
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
.chapter-cart button {
  width: 230px;
  margin-right: 10px;
  margin-bottom: 10px;
}
.chapter-cart {
  margin-left: 35px;
}
</style>
