# 随车行App

描述(包括功能和技术亮点)

(最近在琢磨方案，把服务器的web模块分离出来，就是我想做一个以定位，好友，xx模块为主，然后其他模块为辅的app，所以web模块就是所谓的其他模块，我可以使用h5编写好，然后通过主服务器暴露的接口来实现数据同步(类似微信的授权)，也就是说，我的web模块可以任意，我可以做一个二手交易系统，或者例如滴滴打车系统，然后主模块会把账号信息通过授权的方式给你，这样的话，当我们要开发新的模块的时候，只需要取得主模块的接口即可，这样还有一个好处，就是当我们的主模块足够完善的时候，每次更新我们只需要通过增量更新h5信息，而不用更新整个app。由于自己的js水平不是很高，所以一直卡在js这一块，有木有有兴趣的小伙伴来一起搞一搞)

----------
## guide
预览  
支持功能  
运行环境  
libary   
主模块服务器  
hybrid模块服务器  
搭建说明  
更新日志  
bug提交  
关于我  


## 预览
各个模块po图描述

## 运行环境 
服务器框架ssh(有点笨重，将考虑换成node.js)，然后图片服务器本来自己有写了一个，但是后面还是用回阿里云的oss，因为我的云服务器是腾讯云的学生机，硬盘容量不忍直视啊，阿里云oss对于我这种独立户来说基本等于免费，关于app中模块的前端框架是使用阿里的SuiMobile，感觉对于我这种不是很精通前端的人来说简直就是神器。

1.服务器 
 - MyEclipse 10.7 + Tomcat7
 - J2EE:structs2+hibernate+spring
 - MySQL5.0 
 
2.图片服务器 
 - 阿里云oss
 - node.js    
 
3.android 
 - android studio 1.0+
 - android sdk r16+  
 
4.其他   
 - hybrid前端框架：SuiMobile
 - 交互的方案与框架：


## 模块  
### 第三方框架
 - 网络访问：Retrofit+Rxjava
 - Glide
 - Materia Design
 - 即时通讯|推送：环信sdk
 - 地图：百度地图sdk
 - 图片上传：阿里云oss


### 错误信息处理模块  
 自己写的一个RxJava风格的网络访问异常处理机制：
 - 识别网络访问过程中的各种异常和错误
 - 根据与服务器约定好的错误码进行友好的信息提示
 - 不入侵view层，大大降低耦合度
 - 密钥过期处理：当发现token过期会自动向服务器索取token并重新发起之前失败的那个请求
 - 了解更多请点击：[传送门](http://blog.csdn.net/qq122627018/article/details/51540812%20%E9%98%BF%E6%96%AF%E9%A1%BF "悬停显示")  
 
### 图片压缩缓存模块
 这个模块暂时只用于webview中图片处理相关，因为在native中有glide的存在了，完全没有必要再用自己的  
 - 在webview中发挥这个模块作用的地方有俩个：1.当加载网络图片的时候  2.当从手机本地选取大量图片加载到webview中的时候
 - 在demo中的展示的是第二种情况，当webview需要加载本地图片的时候
 - 三层缓存：1.  
 [github地址](https://github.com/whaoming/WebViewCacheModule "悬停显示")  
 [CSDN地址](http://blog.csdn.net/qq122627018/article/details/53351781 "悬停显示")  
### 全局缓存  
### RecyclerView  
### 其他
 - 本地图片选取：
# 作者介绍



