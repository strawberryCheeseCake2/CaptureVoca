package com.strawberryCodeCake.capturevoca

import android.app.Application
import com.strawberryCodeCake.capturevoca.data.AppContainer
import com.strawberryCodeCake.capturevoca.data.AppDataContainer


class CaptureVocaApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}