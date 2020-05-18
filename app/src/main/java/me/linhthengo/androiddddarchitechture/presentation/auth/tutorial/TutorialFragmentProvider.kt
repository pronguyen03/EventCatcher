package me.linhthengo.androiddddarchitechture.presentation.auth.tutorial

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface TutorialFragmentProvider {
    @ContributesAndroidInjector
    fun provideTutorialFragment(): TutorialFragment
}