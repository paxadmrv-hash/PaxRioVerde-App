# Regras de desofuscação e otimização (R8/ProGuard)
-keep class com.example.paxrioverde.api.** { *; }
-keep class br.com.paxrioverde.app.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn okio.**
-dontwarn io.ktor.**
