<template>
  <div class="container">
    <onlineSearchItem
      class="online-search-item"
      v-for="(result, index) in searchResults"
      :key="index"
      :imgUrl="result.imgUrl"
      :title="result.title"
      :author="result.author"
      :href="result.href"
      :sourceName="result.sourceName"></onlineSearchItem>
  </div>
</template>

<script>
import onlineSearchItem from "./online-search-item.vue";
import request from "../utils/request";

export default {
  name: "onlineSearchList",
  components: {
    onlineSearchItem,
  },

  methods: {
    onlineSearch() {
      //向每个源都发送一个请求
      this.sources.forEach((source) => {
        var _this = this;
        //页码目前默认给第一页。后面需要进行调整
        var searchUrl = "/api/search/" + source.name + "/" + this.searchContent + "/" + 1;
        request({
          url: searchUrl,
          method: "get",
        }).then((response) => {
          response.data.data.forEach((result) => {
            _this.searchResults.push(result);
          });
        });
      });
    },
  },
  data() {
    return {
      searchContent: "",
      sources: [],
      searchResults: [],
    };
  },
  created() {
    this.searchContent = this.$route.params.searchContent;
    this.sources = this.$route.params.sources;
  },
  mounted() {
    this.onlineSearch();
  },
  watch: {
    "$route.params.searchContent": {
      handler(newVal) {
        if (newVal != undefined) {
          this.searchContent = this.$route.params.searchContent;
          this.sources = this.$route.params.sources;
          this.searchResults = [];
          this.onlineSearch();
        }
      },
    },
  },
};
</script>

<style>
.online-search-item {
  width: 20%;
  float: left;
  margin-left: 20px;
}
</style>
