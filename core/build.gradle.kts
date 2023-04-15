import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.graalvm.buildtools.native") version "0.9.20"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
}



repositories {
    mavenLocal()
    mavenCentral()
}

val hutoolVersion = "5.8.16"
val queryDslVersion = "5.0.0"
val knife4jVersion = "4.1.0"

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.graalvm.buildtools.native")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.springframework.plugin.spring")
    apply(plugin = "org.springframework.plugin.jpa")


    group = "io.github.yanfeiwuji"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_17


    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        compileOnly("org.projectlombok:lombok")
        developmentOnly("org.springframework.boot:spring-boot-devtools")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        runtimeOnly("com.mysql:mysql-connector-j")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")

        // queryDsl
        implementation("com.querydsl:querydsl-core:$queryDslVersion")
        implementation("com.querydsl:querydsl-jpa:$queryDslVersion:jakarta")
        annotationProcessor("com.querydsl:querydsl-apt:${queryDslVersion}:jakarta")
        annotationProcessor("jakarta.persistence:jakarta.persistence-api:3.1.0")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

}




