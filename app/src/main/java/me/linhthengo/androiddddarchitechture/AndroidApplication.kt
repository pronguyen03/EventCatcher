package me.linhthengo.androiddddarchitechture

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import me.linhthengo.androiddddarchitechture.core.di.DaggerApplicationComponent
import me.linhthengo.androiddddarchitechture.utils.EncryptedStorageManager
import javax.inject.Inject


class AndroidApplication : Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.create().inject(this)
        EncryptedStorageManager.init(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}