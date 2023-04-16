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
val dozerVersion = "6.5.2"
val keycloakVersion = "21.0.2"

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.graalvm.buildtools.native")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.kapt")


    group = "io.github.yanfeiwuji"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_17


    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        compileOnly("org.projectlombok:lombok")
        developmentOnly("org.springframework.boot:spring-boot-devtools")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        runtimeOnly("com.mysql:mysql-connector-j")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")

        //
        // https://mvnrepository.com/artifact/cn.hutool/hutool-all
        implementation("cn.hutool:hutool-all:$hutoolVersion")

        implementation("io.github.perplexhub:rsql-jpa-specification:6.0.4")

        // 文档
        implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:${knife4jVersion}")
        implementation("org.springframework.boot:spring-boot-starter-validation")

        // dozer
        implementation("com.github.dozermapper:dozer-core:$dozerVersion")
        implementation("javax.xml.bind:jaxb-api:2.3.1")
        implementation("com.sun.xml.bind:jaxb-impl:2.3.1")
        implementation("javax.activation:activation:1.1.1")
        compileOnly("javax.xml.bind:jaxb-api:2.3.1")

        // keycloak
        // https://mvnrepository.com/artifact/org.springframework.security/spring-security-oauth2-resource-server
        implementation("org.springframework.security:spring-security-oauth2-resource-server")
        // https://mvnrepository.com/artifact/org.springframework.security/spring-security-oauth2-jose
        implementation("org.springframework.security:spring-security-oauth2-jose")
        implementation("org.springframework.boot:spring-boot-starter-security")


    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }




    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }


    tasks.withType<Test> {
        useJUnitPlatform()
    }

}




