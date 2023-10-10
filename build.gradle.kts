plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
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
	implementation("org.springframework.boot:spring-boot-starter-data-redis:3.1.3")
	implementation("org.jetbrains:annotations:24.0.0")

	// jwt
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	compileOnly("org.projectlombok:lombok:1.18.26")
	developmentOnly("org.springframework.boot:spring-boot-devtools:3.1.3")
	runtimeOnly("com.mysql:mysql-connector-j:8.0.32")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.3")
	annotationProcessor("org.projectlombok:lombok:1.18.26")

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.3")
	testImplementation("com.github.javafaker:javafaker:1.0.2")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}

val asciidoctorExt: Configuration by configurations.creating
dependencies {
	asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor:3.0.4")
}
tasks {
	val snippetsDir by extra { file("build/generated-snippets") }
	test {
		outputs.dir(snippetsDir)
		useJUnitPlatform()
	}

	asciidoctor {
		inputs.dir(snippetsDir)
		configurations(asciidoctorExt.name)
		dependsOn(test)
		sources {
			include("**/index.adoc")
		}
		baseDirFollowsSourceFile()
	}

	build {
		dependsOn(asciidoctor)
	}
}
