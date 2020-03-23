package me.linhthengo.androiddddarchitechture

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import me.linhthengo.androiddddarchitechture.core.di.ApplicationModule
import me.linhthengo.androiddddarchitechture.core.di.DaggerApplicationComponent
import timber.log.Timber


class AndroidApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}