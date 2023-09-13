plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.rightpair"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.3")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.1.3")
	implementation("org.springframework.boot:spring-boot-starter-security:3.1.3")
	implementation("org.springframework.boot:spring-boot-starter-web:3.1.3")
	compileOnly("org.projectlombok:lombok:1.18.26")
	developmentOnly("org.springframework.boot:spring-boot-devtools:3.1.3")
	runtimeOnly("com.mysql:mysql-connector-j:8.0.32")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.3")
	annotationProcessor("org.projectlombok:lombok:1.18.26")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.3")
	testImplementation("com.github.javafaker:javafaker:1.0.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
