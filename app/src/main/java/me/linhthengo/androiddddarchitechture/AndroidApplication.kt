package me.linhthengo.androiddddarchitechture

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import me.linhthengo.androiddddarchitechture.core.di.DaggerApplicationComponent


class AndroidApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerApplicationComponent.builder().build()
}