# ToDoKiReader—Web

## 目前处于项目开发状态。但暂时停滞

## 项目介绍

一个基于Web的漫画阅读抓取工具。后端采用Spring全家桶 + Selenium，前端采用vue + picocss

目前有两个源可以抓取。因为MaoFly没能拿到api，所以只能尝试采用Selenium抓取。但效果不佳。受网络环境波动影响较大。后面可能会采用API的方式来获取。

采用了对象池来管理Selenium的chrome driver，需要将chromeDriver.exe放置到yaml中配置的driver文件夹下。在获取到Url之后，会异步的启用线程池交给下载器来下载。同时返回图片数量，在消息队列中放置开始和结束标志，以便于前端判断图片缓存状态。

目前感觉这种机制的开销很大。且前后端通信非常的麻烦。后面可能考虑制作成一个客户端程序。
