import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	id("java")
	id("java-library")
	id("maven-publish")
	id("com.github.johnrengelman.shadow") version "5.2.0" apply false
	id("org.shipkit.shipkit-auto-version") version "1.1.1"
	id("org.shipkit.shipkit-changelog") version "1.1.4"
	id("org.shipkit.shipkit-github-release") version "1.1.4"
	id("dev.minco.gradle.defaults-plugin") version "0.2.8"
}

apply(from = "properties.gradle")
apply(from = "$rootDir/gradle/shipkit.gradle")

java {
	withSourcesJar()
	withJavadocJar()
}

minimallyCorrectDefaults {
	configureProject(project)
}

val releasing = project.hasProperty("releasing")
if (!releasing) {
	version = "$version-SNAPSHOT"
}

allprojects {
	repositories {
		exclusiveContent {
			forRepository {
				maven { url = uri("https://maven.minco.dev/") }
			}
			filter {
				includeGroupByRegex("dev\\.minco.*")
				includeGroupByRegex("me\\.nallar.*")
				includeGroupByRegex("org\\.minimallycorrect.*")
			}
		}
		mavenCentral()
	}

	tasks.withType<Test> {
		maxParallelForks = Runtime.getRuntime().availableProcessors() / 2

		testLogging {
			events(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
			exceptionFormat = TestExceptionFormat.FULL
			showCauses = true
			showExceptions = true
			showStackTraces = true
			showStandardStreams = true
		}
	}

	configurations.all {
		resolutionStrategy {
			failOnVersionConflict()
			failOnNonReproducibleResolution()
		}
	}
}


dependencies {
	testImplementation("junit:junit:4.13.2")
	implementation("me.nallar.whocalled:WhoCalled:1.1")
	api("dev.minco:java-transformer:1.10.1")

	val lombok = "org.projectlombok:lombok:1.18.20"
	implementation(lombok)
	annotationProcessor(lombok)
	testAnnotationProcessor(lombok)
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}
}

publishing {
	repositories {
		System.getenv("DEPLOYMENT_REPO_PASSWORD")?.let { deploymentRepoPassword ->
			maven {
				url = if (releasing) {
					name = "minco.dev_releases"
					uri(System.getenv("DEPLOYMENT_REPO_URL_RELEASE"))
				} else {
					name = "minco.dev_snapshots"
					uri(System.getenv("DEPLOYMENT_REPO_URL_SNAPSHOT"))
				}
				credentials {
					username = System.getenv("DEPLOYMENT_REPO_USERNAME")
					password = deploymentRepoPassword
				}
			}
		}
	}
}
