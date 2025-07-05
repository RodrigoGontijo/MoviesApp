import org.gradle.testing.jacoco.tasks.JacocoReport

plugins.apply("jacoco")

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    val fileFilter = listOf(
        "**/R.class", "**/R$*.class",
        "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*",
        "**/di/**", "**/App.*"
    )

    // Inclui apenas arquivos que terminam com ViewModel
    val includeViewModelOnly = listOf("**/*ViewModel.class", "**/*ViewModel\$*.class")

    val kotlinDebugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        include(includeViewModelOnly)
        exclude(fileFilter)
    }

    val javaDebugTree = fileTree("${buildDir}/intermediates/javac/debug") {
        include(includeViewModelOnly)
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(listOf(mainSrc)))
    classDirectories.setFrom(files(listOf(kotlinDebugTree, javaDebugTree)))
    executionData.setFrom(fileTree(buildDir) {
        include(
            "jacoco/testDebugUnitTest.exec",
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
        )
    })

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}