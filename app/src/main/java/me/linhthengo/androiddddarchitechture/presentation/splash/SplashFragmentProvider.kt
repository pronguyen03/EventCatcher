package me.linhthengo.androiddddarchitechture.presentation.splash

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SplashFragmentProvider {

    @ContributesAndroidInjector
    fun provideSplashFragment(): SplashFragment
}
