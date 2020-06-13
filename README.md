
# IJToolsUsingZXing

まだ作成途中です。<br>
Still in the process of being created.

## 日本語の説明

1D/2Dバーコードの読み取りと生成ができる「ZXing」の一部を利用するImageJ用プラグインを作りました。

## 目的

* 手軽にZXingを利用したい
* ZXingの処理の結果を簡単に確認したい
* ImageJにない機能を追加したい

## ビルド方法

上のファイルはNetBeansでビルドできます。[こちら](https://waku-take-a.github.io/NetBeans%25E3%2581%25AB%25E3%2582%2588%25E3%2582%258BPlugin%25E4%25BD%259C%25E6%2588%2590.html) のサイトを参考にしてください。

## バイナリー

作成したプラグインのバイナリー(`IJToolUsingZXing_***zip`)を[こちら](https://github.com/WAKU-TAKE-A/IJToolUsingZXing/releases)に置いておきます。<br>是非使ってみてください。

使い方は、以下の通りです。

(1) [ZXing core](https://mvnrepository.com/artifact/com.google.zxing/core) と、[ZXing Java SE extensions](https://mvnrepository.com/artifact/com.google.zxing/javase) をダウンロードして、ImageJのpluginsフォルダにコピーします。<br> 
(3.40で試しています)

(2) リリースからダウンロードしたZIPファイルを展開し、ZXingフォルダをpluginsフォルダにコピーしてください。

(3) ImageJを再起動してください。

## English description

I have created a plugin for ImageJ that uses part of "ZXing" that can read and generate 1D/2D barcodes.

## Purpose

* Easy to use ZXing.
* Easy to check the result of ZXing processing.
* Easy to add a function that ImageJ does not have.

## Build method

The above file can be built with NetBeans. [Here](https://waku-take-a.github.io/NetBeans%25E3%2581%25AB%25E3%2582%2588%25E3%2582%258BPlugin%25E4%25BD%259C%25E6%2588%2590.html) . Please refer to the site.

## binary

Put the created plugin binary (`IJToolUsingZXing_***zip`) in [here](https://github.com/WAKU-TAKE-A/IJToolUsingZXing/releases). <br>Please try it out.

The usage is as follows.

(1) [ZXing core](https://mvnrepository.com/artifact/com.google.zxing/core) and [ZXing Java SE extensions](https://mvnrepository.com/artifact/com.google.zxing/javase) and copy it to the ImageJ plugins folder.<br>
(I am trying with 3.40)

(2) Extract the ZIP file downloaded from the release page, and copy the ZXing folder to the plugins folder.

(3) Restart ImageJ.