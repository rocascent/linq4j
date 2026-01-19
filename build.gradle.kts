plugins {
    kotlin("jvm") version "2.3.0"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.rocascent"
version = "1.0.2"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

mavenPublishing {
    publishToMavenCentral(true)

    signAllPublications()

    coordinates(group.toString(), "linq4j", version.toString())

    pom {
        name = "Linq4j"
        description = "Use Kotlin Sequence with LINQ-style in Java."
        inceptionYear = "2025"
        url = "https://github.com/rocascent/linq4j"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "rocascent"
                name = "rocascent"
                email = "gn3po4g@outlook.com"
                organization = "GitHub"
                organizationUrl = "https://github.com/rocascent"
            }
        }
        scm {
            url = "https://github.com/rocascent/linq4j/"
            connection = "scm:git:git://github.com/rocascent/linq4j.git"
            developerConnection = "scm:git:ssh://git@github.com/rocascent/linq4j.git"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}