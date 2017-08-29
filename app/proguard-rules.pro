# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/mt/AndroidNew/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor

-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor

-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptorWithTypeParameters

-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationDescriptor

-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.impl.PropertyDescriptorImpl

-dontwarn kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder

-dontwarn kotlin.reflect.jvm.internal.impl.resolve.OverridingUtil

-dontwarn kotlin.reflect.jvm.internal.impl.types.DescriptorSubstitutor

-dontwarn kotlin.reflect.jvm.internal.impl.types.DescriptorSubstitutor

-dontwarn kotlin.reflect.jvm.internal.impl.types.TypeConstructor

-dontwarn javax.management.**

-dontwarn javax.xml.**

-dontwarn org.apache.**

-dontwarn org.slf4j.**
-dontwarn android.support.v7.**
-dontwarn com.github.siyamed.**
-keep class com.github.siyamed.shapeimageview.**{ *; }

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
