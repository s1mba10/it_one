// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.sonarqube") version "3.3"
}

buildscript {
    repositories {
        jcenter()
        maven {
            url  =  uri("https://maven.google.com/")
            name = "Google"
        }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.2")
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:2.7.1")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}