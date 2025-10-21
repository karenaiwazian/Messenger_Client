package com.aiwazian.messenger

import android.app.Application
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.instream.MobileInstreamAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) { }
        MobileInstreamAds.setAdGroupPreloading(true)
    }
}