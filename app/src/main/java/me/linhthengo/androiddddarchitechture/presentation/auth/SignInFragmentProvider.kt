package me.linhthengo.androiddddarchitechture.presentation.auth

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SignInFragmentProvider {

    @ContributesAndroidInjector
    fun provideSignInFragment(): SignInFragment
}