# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#指定代码的压缩级别。指定执行几次优化，默认情况下，只执行一次优化。
#执行多次优化可以提高优化的效果
#但是，如果执行过一次优化之后没有效果，就会停止优化，剩下的设置次数不再执行
-optimizationpasses 5

# 混淆时所采用的算法,后面的参数是一个过滤器,这个过滤器是谷歌推荐的算法,一般做不更改
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#忽略警告 不忽略可能会打包失败
-ignorewarnings

# 是否使用大小写混合
-dontusemixedcaseclassnames

-keep class com.dc.ztllib.**

#保留protected方法
-keep public class * {
    public protected *;
}

-keepattributes *Annotation*

#保留本地方法
-keepclassmembers class * {
    public <methods>;
}