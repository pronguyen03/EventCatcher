package me.linhthengo.androiddddarchitechture.core.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.linhthengo.androiddddarchitechture.presentation.SplashFragment
import me.linhthengo.androiddddarchitechture.presentation.auth.SignInFragment
import me.linhthengo.androiddddarchitechture.presentation.auth.SignUpFragment
import me.linhthengo.androiddddarchitechture.presentation.home.HomeFragment

@Module
interface FragmentModule {
    @ContributesAndroidInjector
    fun splashFragment(): SplashFragment

    @ContributesAndroidInjector
    fun homeFragment(): HomeFragment

    @ContributesAndroidInjector
    fun signInFragment(): SignInFragment

    @ContributesAndroidInjector
    fun signUpFragment(): SignUpFragment
}