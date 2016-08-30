# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Programy\android-studio\sdk/tools/proguard/proguard-android.txt
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

#-printmapping ..\outputs\mapping.txt

-optimizationpasses 5

-keepattributes *Annotation*
-keepattributes Signature

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }


-dontwarn com.google.appengine.**
-dontwarn java.nio.**
-dontwarn org.codehaus.**
-dontwarn rx.**
-dontwarn javax.**
-dontwarn com.squareup.**
-dontwarn butterknife.**

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keep public class javax.net.ssl.**
-keepclassmembers public class javax.net.ssl.** {
  *;
}

-keep public class org.apache.http.**
-keepclassmembers public class org.apache.http.** {
  *;
}

#eventbus
-keepclassmembers class ** {
    public void onEvent*(**);
}


#google maps
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}


#butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}


#keep classes that have to stay unchanged because of reflection (gson, animations, ...)
#TODO add app specific packages
-keep class sun.misc.Unsafe { *; }

-keep class cz.ackee.androidskeleton.model.** {*;}
-keep class cz.ackee.androidskeleton.rest.** {*;}
-keep class cz.ackee.androidskeleton.ui.** {*;}
-keep class cz.ackee.androidskeleton.utils.HeightSetter

#To maintain custom components names that are used on layouts XML.
#Uncomment if having any problem with the approach below
#-keep public class custom.components.package.and.name.**

-keepclasseswithmembers public class * extends android.view.View
