import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java")
    id("org.springframework.boot") version "3.2.1" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
    id("net.ltgt.errorprone") version "4.1.0" apply false
}

allprojects {
    group = "com.example"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "net.ltgt.errorprone")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Spotless - Google Java Format 설정
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            googleJavaFormat("1.19.2")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    dependencies {
        // Error Prone 컴파일러
        "errorprone"("com.google.errorprone:error_prone_core:2.24.1")
    }

    // Error Prone 컴파일러 옵션 설정
    tasks.withType<JavaCompile>().configureEach {
        options.errorprone {
            // Error Prone 활성화
            isEnabled.set(true)
            // 모든 경고를 에러로 처리하지 않음 (점진적 도입)
            allErrorsAsWarnings.set(true)
            // 특정 검사 비활성화 (필요시 추가)
            disable("MissingSummary")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}