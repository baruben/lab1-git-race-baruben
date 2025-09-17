import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jpa)
}

group = "es.unizar.webeng"
version = "2025-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    developmentOnly(libs.spring.boot.devtools)
    implementation(libs.bootstrap)
    implementation(libs.jackson.module.kotlin)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation("org.springframework.security:spring-security-test")
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:8.0.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    runtimeOnly(libs.h2)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootRun> {
	sourceResources(sourceSets["main"])
}