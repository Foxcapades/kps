import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.7.0"
  id("org.jetbrains.dokka") version "1.7.0"
  `maven-publish`
  signing
}

group = "io.foxcapades.lib.kps"
version = "1-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8

  withJavadocJar()
  withSourcesJar()
}

tasks.dokkaHtml {
  outputDirectory.set(file("$rootDir/build/docs/kpd"))
}

publishing {
  repositories {
    maven {
      name = "GitHub"
      url = uri("https://maven.pkg.github.com/foxcapades/kps")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }

    maven {
      name = "Sonatype"
      url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
      credentials {
        username = project.findProperty("nexus.user") as String? ?: System.getenv("NEXUS_USER")
        password = project.findProperty("nexus.pass") as String? ?: System.getenv("NEXUS_PASS")
      }
    }
  }

  publications {
    create<MavenPublication>("gpr") {
      from(components["java"])
      pom {
        name.set("Primitive Deques")
        description.set("Provides deque implementations for dealing with Kotlin's primitives (without the boxing).")
        url.set("https://github.com/foxcapades/kps")

        licenses {
          license {
            name.set("MIT")
          }
        }

        developers {
          developer {
            id.set("epharper")
            name.set("Elizabeth Paige Harper")
            email.set("foxcapades.io@gmail.com")
            url.set("https://github.com/foxcapades")
          }
        }

        scm {
          connection.set("scm:git:git://github.com/foxcapades/kps.git")
          developerConnection.set("scm:git:ssh://github.com/foxcapades/kps.git")
          url.set("https://github.com/foxcapades/kps")
        }
      }
    }
  }
}

signing {
  useGpgCmd()

  sign(configurations.archives.get())
  sign(publishing.publications["gpr"])
}

