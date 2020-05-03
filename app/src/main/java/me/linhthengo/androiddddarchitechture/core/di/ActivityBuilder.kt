package me.linhthengo.androiddddarchitechture.core.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.linhthengo.androiddddarchitechture.presentation.MainActivity
import me.linhthengo.androiddddarchitechture.presentation.auth.profile.ProfileFragmentProvider
import me.linhthengo.androiddddarchitechture.presentation.auth.signin.SignInFragmentProvider
import me.linhthengo.androiddddarchitechture.presentation.auth.signup.SignUpFragmentProvider
import me.linhthengo.androiddddarchitechture.presentation.home.HomeFragmentProvider
import me.linhthengo.androiddddarchitechture.presentation.splash.SplashFragmentProvider

@Module
interface ActivityBuilder {

    @ContributesAndroidInjector(
        modules = [
            SplashFragmentProvider::class,
            SignInFragmentProvider::class,
            SignUpFragmentProvider::class,
            HomeFragmentProvider::class,
            ProfileFragmentProvider::class
        ]
    )
    fun buildMainActivity(): MainActivity
}