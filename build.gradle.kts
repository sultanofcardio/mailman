import java.net.URI

plugins {
    kotlin("jvm") version "1.3.61"
    id("maven-publish")

}

group = "com.sultanofcardio"
version = "3.1.0"

val projectName = "mailman"

val jar by tasks.getting(Jar::class) {
    baseName = projectName
}

repositories {
    mavenCentral()
    maven { url = URI.create("https://repo.sultanofcardio.com/artifactory/sultanofcardio") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

val sultanofcardioUser: String by project
val sultanofcardioPassword: String by project
val sultanofcardioUrl: String by project

publishing {
    repositories {
        maven {
            name = "sultanofcardio"
            credentials {
                username = sultanofcardioUser
                password = sultanofcardioPassword
            }
            url = URI.create(sultanofcardioUrl)
        }
    }

    publications {
        register("binary", MavenPublication::class) {
            artifactId = projectName
            version = project.version as String
            from(components["java"])
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}