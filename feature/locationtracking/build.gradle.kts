import com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.muzafferatmaca.locationtracking"
    compileSdk = BuildAndroidConfig.COMPILE_SDK

    defaultConfig {
        minSdk = BuildAndroidConfig.MIN_SDK

        testInstrumentationRunner = BuildAndroidConfig.ANDROID_JUNIT_RUNNER
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName(BuildTypes.Android.release) {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            matchingFallbacks += listOf("devRelease", "prodRelease")
        }
        getByName(BuildTypes.Android.debug) {
            matchingFallbacks += listOf("devDebug", "prodDebug")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = BuildAndroidConfig.JVM_TARGET
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    // Fix: [Ksp] InjectProcessingStep was unable to process 'test' because 'error.NonExistentClass' could not be resolved.
    // https://github.com/google/dagger/issues/4097#issuecomment-1763781846
    // Note, This is a temporary fix and needs to wait for the official official fix
    androidComponents {
        onVariants(selector().all()) { variant ->
            afterEvaluate {
                project.tasks.getByName("ksp" + variant.name.capitalized() + "Kotlin") {
                    val dataBindingTask =
                        project.tasks.getByName("dataBindingGenBaseClasses" + variant.name.capitalized()) as DataBindingGenBaseClassesTask
                    (this as AbstractKotlinCompileTool<*>).setSource(dataBindingTask.sourceOutFolder)
                }
            }
        }
    }
}

dependencies {

    implementation(projects.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    //Map
    implementation(libs.google.maps)
    implementation(libs.google.location)
    implementation(libs.google.maps.utils)
    implementation(libs.google.maps.places)
    //Datastore
    implementation(libs.androidx.datastore)
    //LifeCycleViewModel
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.service)
    //Coroutine
    implementation(libs.coroutine.core)
    implementation(libs.coroutine.android)
    implementation(libs.coroutine.playServices)

}