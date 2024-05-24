import com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.muzafferatmaca.geotrack"
    compileSdk = BuildAndroidConfig.COMPILE_SDK

    defaultConfig {
        applicationId = BuildAndroidConfig.APPLICATION_ID
        minSdk = BuildAndroidConfig.MIN_SDK
        targetSdk = BuildAndroidConfig.TARGET_SDK

        testInstrumentationRunner = BuildAndroidConfig.ANDROID_JUNIT_RUNNER
    }

    buildTypes {
        getByName(BuildTypes.Android.release) {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName(BuildTypes.Android.debug) {
            isDebuggable = true
            isMinifyEnabled = false
        }
    }

    flavorDimensions += "version"
    productFlavors {

        create("dev") {

            dimension = "version"
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"

            versionCode = BuildAndroidConfig.VERSION_CODE
            versionName = BuildAndroidConfig.VERSION_NAME

            resValue(type ="string",name = "MAP_API_KEY", value = "\"${BuildAndroidConfig.MAP_API_KEY}\"")

            resValue(type = "string", name =  "app_name", value =  "${BuildAndroidConfig.APP_NAME}$versionNameSuffix")
        }

        create("prod") {

            dimension = "version"
            versionNameSuffix = ""
            applicationIdSuffix = ""

            versionCode = BuildAndroidConfig.VERSION_CODE
            versionName = BuildAndroidConfig.VERSION_NAME

            resValue(type ="string",name = "MAP_API_KEY", value = "\"${BuildAndroidConfig.MAP_API_KEY}\"")

            resValue(type = "string", name =  "app_name", value =  "${BuildAndroidConfig.APP_NAME}$versionNameSuffix")
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
    implementation(projects.feature.locationtracking)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //LifeCycleViewModel
    implementation(libs.androidx.lifecycle.viewmodel)


}