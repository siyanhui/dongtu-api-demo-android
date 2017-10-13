# 简介

本demo是表情搜搜的使用示例，示范了表情搜搜的三个使用场景，分别是：

- 根据用户输入即时联想场景

![img](/img/quater_screen.jpg)  

- 键盘搜索场景

![img](/img/half_screen.jpg)

- 全屏搜索场景

![img](/img/full_screen.jpg)

# API

生产API地址为：http://open-api.dongtu.com:8081/open-api/

使用到的API有三个：

- 热门标签：netword/hot/
- 流行表情：trending
- 搜索：   emojis/net/search

API文档详见 http://api-doc.dongtu.com/dongtu/

# demo主要类介绍

## model

1. BQSSWebSticker：  接口中返回的表情
2. BQSSHotTag：      热门标签

## API

BQSSSearchApi中对应的三个方法

1.getSearchStickers    获取某个关键词对应的网络表情

2.getTrendingStickers   获取热门网络表情

3.getHotTagStickers   获取热门标签

# 运行demo

把demo代码下载到本地后，用Android Studio打开，然后在根目录的local.properties中添加上

    bqmm.appID＝您申请的appID

即可成功运行demo。
