package com.cap.withsullivan

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "e8f3732d36d621e7d5afb540cd9fd88c")
    }
}