# ApkDex2jar

![ApkDex2jar](https://github.com/kadisting/ApkDex2jar/blob/master/image/WX20191207-171805%402x.png)

### 说明
基于Dex2jar和apktool、jd-gui等第三方工具，实现对apk进行快速反编译操作。

简化反编译过程，告别繁琐的文件夹切换操作，一拖即可反编译。

本工具仅用于辅助研发测试，或普通的apk反编译。
对于已使用第三方加固过的apk，可能会反编译失败，或者无法进行反编译。


### 环境
* UI界面基于java8+javafx
* IDEA

### 工具版本
* Dex2jar：2.0
* apktool：2.4.1
* JD-GUI：1.6.5

以上工具已经内置到程序中，无需再设置


### 使用
* 运行后将apk直接拖入到程序界面。等待反编译完成。
* 反编译完成后，会在apk所在的同级文件夹下生成一个以_ApkDex2jar结尾的文件夹。反编译后的jar及资源文件保存在此文件夹中。
* 点击"查看反编译文件"会打开反编译后对应的文件夹。
* 点击JD-GUI图标会打开Java Decompiler，将上一步中的classes.dex.jar文件拖入其中，即可查看对应的java代码。