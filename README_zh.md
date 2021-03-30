<div style="font-size: 40px; font-weight: bold;text-align: center;">Bookmark Tomb</div>
<div style="color: grey; font-size: 30px; font-weight: normal;text-align: center;">签坟</div>

> 本项目是一个书签同步系统，你现在访问的时我们的后端仓库。

你可以前往我们的[主页](https://bookmarktomb.github.io/BookmarkTomb_Docs)查看帮助文档(暂时还不够完善)。

## 简介

后端项目是基于SpringBoot，使用MongoDB数据库存储数据. 本项目依赖于Java(>=11)。

主要实现简单罗列在下方: 

|功能|涉及技术|
|:----:|:----:|
|框架|SpringBoot|
|数据库|MongoDB|
|认证与鉴权|Spring Security with Token|
|API 文档|Swagger with [knife4j](https://github.com/xiaoymin/swagger-bootstrap-ui)|

##  开发调试

1. 克隆本项目
   
    `git clone https://mygitea.fallen-angle.com/fallen-angle/bookmark_tomb_back`
   
2. 将本项目带入IDEA或其他编辑器。
3. 设置Java开发环境的版本为11或者更新。
4. 使用Maven从仓库中拉取依赖。
5. 最后，你需要初始化配置文件，可以通过URL配置，也可以直接更改配置文件，具体请参照[这里](https://bookmarktomb.github.io/BookmarkTomb_Docs/#develop)。

## 其他

本系统在设计之初就存在很多缺陷，可以改进的地方有很多，例如：

- 使用 `ChangeStream` 代替直接使用代码来对数据进行预处理。
- 可以简化很多命名方式.
- 也许MySQL更适合这类数据的存储.
- 等等等等……

之后，我们可能会在下个版本重构整个项目来改善这些问题。