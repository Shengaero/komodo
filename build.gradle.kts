/*
 * Copyright 2019 Kaidan Gustave
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  idea
  `java-library`
  kotlin("jvm")
}

allprojects {
  group = "me.kgustave"
  version = "1.0.0"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
  sourceSets {
    all {
      languageSettings.useExperimentalAnnotation("kotlin.Experimental")
    }
  }
}

tasks {
  wrapper {
    gradleVersion = "5.0"
  }
}

evaluationDependsOnChildren()

allprojects {
  repositories {
    jcenter()
  }

  val junitVersion: String by ext

  dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk7"))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
  }

  tasks {
    withType<KotlinCompile> {
      kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.3"
      }
    }

    test {
      useJUnitPlatform()
      testLogging.showStandardStreams = true
    }
  }
}
