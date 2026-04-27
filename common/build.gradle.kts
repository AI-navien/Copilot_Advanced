plugins {
    id("java")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}
