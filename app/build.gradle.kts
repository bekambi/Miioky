plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.bekambimouen.miiokychallenge"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bekambimouen.miiokychallenge"
        minSdk = 24
        targetSdk = 34
        versionCode = 14
        versionName = "1.1.63"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.play.services.tagmanager.v4.impl)
    implementation(libs.filament.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //    Splash screen API
    implementation(libs.androidx.core.splashscreen)
    // firebase
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.database)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.google.firebase.core)

    // like
    implementation(libs.likebutton)


    // date picker
    implementation(libs.datepicker)
    // country picker
    implementation(libs.country.picker.android)
    // pagination
    implementation(libs.paginated.recyclerview)
    // expandableTextView
    implementation(libs.viewmoretextview)
    // circle imageview
    implementation(libs.circleimageview)
    // custom progressbar
    implementation(libs.android.spinkit)
    // gson
    implementation(libs.gson)
    implementation(libs.androidx.multidex)
    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    // translate language
    implementation(libs.translateapi)
    // detect text language
    implementation(libs.language.id)
    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    // compression video
    implementation(files("libs/isoparser-1.0.6.jar"))
    implementation(files("libs/aspectjrt-1.7.3.jar"))
    // page indicator (point d'incation du sweep recyclerView)
    implementation(libs.pageindicator)
    // story library
    implementation(libs.storiesprogressview)
    // countries flags
    implementation(libs.worldcountrydata)
    // recording message animation
    implementation(libs.recordview)
    // for search bar
    implementation(libs.volley)
    // Agrandir et réduire la photo (photoView)
    implementation(libs.photoview)
    // double tap to like video wjth animation
    implementation(libs.instalikeview)
    // recyclerview sliding indicator
    implementation(libs.scrollingpagerindicator)
    // insta cropper
    implementation(libs.croppernocropper)
    // cropper imageView
    implementation(libs.android.crop)
    // exoplayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer.smoothstreaming)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.exoplayer.hls)
    // danikulacache
    implementation(libs.slf4j.android)
    implementation(libs.universal.image.loader)
    // video trimmer
    implementation(libs.localization)
    implementation(libs.ffmpeg.kit.min)
    // zoom view in recyclerview (ZoomHelper)
    implementation(libs.zoomhelper)
    // update application
    implementation(libs.app.update)
    // comunication entre les composants d'une application
    implementation(libs.eventbus)
}