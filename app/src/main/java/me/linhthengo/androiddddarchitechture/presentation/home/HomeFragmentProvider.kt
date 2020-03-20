package me.linhthengo.androiddddarchitechture.presentation.home

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface HomeFragmentProvider {

    @ContributesAndroidInjector
    fun provideHomeFragment(): HomeFragment
}