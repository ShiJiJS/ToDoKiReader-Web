<template>
  <div class="container">
    <img :src="url" v-for="(url, index) in imageList" :key="index" />
  </div>
</template>

<script>
import request from "../utils/request";
import requestConst from "../const/requestConst";

export default {
  name: "onlineReadPage",

  methods: {
    getAmount() {
      var _this = this;
      var data = {
        title: this.title,
        chapter: this.chapter,
        url: this.url,
        sourceName: this.sourceName,
      };
      request({
        url: "/api/amountOfImages",
        method: "post",
        headers: {
          "Content-Type": "application/json",
        },
        data: JSON.stringify(data),
      }).then((response) => {
        _this.amount = response.data.data.amount;
        _this.titleNumber = response.data.data.titleNumber;
        _this.chapterNumber = response.data.data.chapterNumber;
        _this.fileExtension = response.data.data.fileExtension;
        this.getImages();
      });
    },

    async getImages() {
      if (this.amount == -1) {
        //图片数量未知，依次获取
        let _this = this;
        for (let i = 1; ; i++) {
          //阻塞检查图片是否缓存完成
          const response = await request({
            url: "/api/checkImgStatus/" + _this.titleNumber + "/" + _this.chapterNumber + "/" + i + "." + _this.fileExtension,
            method: "get",
          });
          //判断code
          let code = response.data.data.code;
          if (code == requestConst.IMG_GET_OK) {
            //成功拿到图片
            //向imageList中添加url
            this.imageList.push(
              requestConst.baseURL + "/" + "temp" + "/" + this.titleNumber + "/" + this.chapterNumber + "/" + i + "." + this.fileExtension
            );
          } else if (code == requestConst.CHAPTER_OVER) {
            //章节结束
            break;
          } else if (code == requestConst.IMG_GET_OVERTIME) {
            //超时
            continue; //继续下一张
          }
        }
      } else if (this.amount != 0) {
        //如果数量可以确定
        for (let i = 1; i <= this.amount; i++) {
          //阻塞等待图片缓存完成
          const response = await request({
            url: "/api/checkImgStatus/" + this.titleNumber + "/" + this.chapterNumber + "/" + i + "." + this.fileExtension,
            method: "get",
          });
          //判断code
          let code = response.data.data.code;
          if (code == requestConst.IMG_GET_OK) {
            //成功拿到图片
            //向imageList中添加url
            this.imageList.push(
              requestConst.baseURL + "/" + "temp" + "/" + this.titleNumber + "/" + this.chapterNumber + "/" + i + "." + this.fileExtension
            );
          } else if (code == requestConst.IMG_GET_OVERTIME) {
            //超时
            continue; //继续下一张
          }
        }
      } else {
        console.log("该页无图片");
      }
    },
  },
  data() {
    return {
      url: "",
      amount: 0,
      title: "",
      chapter: "",
      sourceName: "",
      titleNumber: 0,
      chapterNumber: 0,
      imageList: [],
      fileExtension: "",
    };
  },
  created() {
    this.url = this.$route.params.url;
    this.title = this.$route.params.title;
    this.chapter = this.$route.params.chapter;
    this.sourceName = this.$route.params.sourceName;
  },
  mounted() {
    this.getAmount();
  },

  watch: {
    "$route.params.chapter": {
      handler(newVal) {
        if (newVal != undefined) {
          this.url = this.$route.params.url;
          this.title = this.$route.params.title;
          this.chapter = this.$route.params.chapter;
          this.sourceName = this.$route.params.sourceName;
          this.imageList = [];
          this.getAmount();
        }
      },
    },
  },
};
</script>

<style scoped>
body {
  background-color: rgb(18, 18, 18);
}

img {
  width: 100%;
}
</style>
