# Proguard rules for LingoDom
-keepattributes *Annotation*

# Keep Compose classes
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Keep data classes used with DataStore
-keep class com.lingodom.app.core.model.** { *; }
