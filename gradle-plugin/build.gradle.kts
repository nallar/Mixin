plugins {
	id("groovy")
	id("java")
	id("java-library")
	id("maven-publish")
	id("java-gradle-plugin")
	id("com.github.johnrengelman.shadow")
}

apply(from = "groovy.gradle")

dependencies {
	val lombok = "org.projectlombok:lombok:1.18.20"
	implementation(lombok)
	annotationProcessor(lombok)
	testAnnotationProcessor(lombok)
}
