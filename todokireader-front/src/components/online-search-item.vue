<template>
  <div>
    <article>
      <header>{{ title }}</header>
      <a @click="toOnlineChapter()"><img :src="imgUrl" /></a>
      <footer>
        <p>作者{{ author }}</p>
        <p>源:{{ sourceName }}</p>
      </footer>
    </article>
  </div>
</template>

<script>
export default {
  name: "onlineSearchItem",
  props: {
    imgUrl: {
      type: String,
      required: true,
    },
    title: {
      required: true,
    },
    author: {
      type: String,
      required: true,
    },
    href: {
      type: String,
      required: true,
    },
    sourceName: {
      type: String,
      required: true,
    },
  },
  data() {
    return {};
  },
  methods: {
    toOnlineChapter() {
      var _this = this;
      this.$router.push({
        path: "/onlineChapter",
        name: "onlineChapterPage",
        params: {
          url: _this.href,
          sourceName: _this.sourceName,
        },
      });
    },
  },
};
</script>

<style>
header {
  height: 150px;
}
</style>
