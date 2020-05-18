package me.linhthengo.androiddddarchitechture.presentation.event

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ListEventsFragmentProvider {
    @ContributesAndroidInjector
    fun provideListEventsFragment(): ListEventsFragment
}