package me.linhthengo.androiddddarchitechture.presentation.auth.profile

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ProfileFragmentProvider {
    @ContributesAndroidInjector
    fun provideProfileFragment(): ProfileFragment
}