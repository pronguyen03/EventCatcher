package me.linhthengo.androiddddarchitechture.core.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.linhthengo.androiddddarchitechture.presentation.MainActivity

@Module
interface ActivityModule {
    @ContributesAndroidInjector
    fun mainActivity(): MainActivity
}