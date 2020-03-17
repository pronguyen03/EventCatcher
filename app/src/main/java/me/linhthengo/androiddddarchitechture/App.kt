package me.linhthengo.androiddddarchitechture

import android.app.Application
import me.linhthengo.androiddddarchitechture.utils.EncryptedSharedPreferenceManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        EncryptedSharedPreferenceManager.init(this)
    }

}