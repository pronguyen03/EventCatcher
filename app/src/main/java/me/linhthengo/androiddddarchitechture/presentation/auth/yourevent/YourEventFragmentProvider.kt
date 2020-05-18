package me.linhthengo.androiddddarchitechture.presentation.auth.yourevent

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface YourEventFragmentProvider {
    @ContributesAndroidInjector
    fun provideTutorialFragment(): YourEventFragment
}