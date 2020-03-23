package me.linhthengo.androiddddarchitechture.presentation.auth.signup

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SignUpFragmentProvider {

    @ContributesAndroidInjector
    fun providerSignUpFragment(): SignUpFragment
}