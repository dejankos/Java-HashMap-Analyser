plugins {
    java
    jacoco
}

group = "io.github.dejankos"
version = "1.1-SNAPSHOT"
description = "hash-map-analyser"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
    }
}