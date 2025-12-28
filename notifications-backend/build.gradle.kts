plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
    id("com.google.devtools.ksp") version "1.9.25-1.0.20"
    id("io.micronaut.application") version "4.6.1"
    id("com.gradleup.shadow") version "8.3.9"
    id("io.micronaut.aot") version "4.6.1"
    id("jacoco")
}

version = "0.1"
group = "ntd.challenge"

repositories {
    mavenCentral()
}

dependencies {
    // KSP
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    ksp("io.micronaut.validation:micronaut-validation-processor")
    ksp("io.micronaut.data:micronaut-data-processor")

    // Kotlin & Micronaut
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut.reactor:micronaut-reactor")

    // Database
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("net.datafaker:datafaker:2.4.0")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    // Messaging (JMS + SQS)
    implementation("io.micronaut.jms:micronaut-jms-core")
    implementation("io.micronaut.jms:micronaut-jms-sqs")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
    implementation("com.amazonaws:amazon-sqs-java-messaging-lib:2.1.3")

    // Utils
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Testing
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = "ntd.challenge.Application"
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
}

graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("ntd.challenge.*")
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}