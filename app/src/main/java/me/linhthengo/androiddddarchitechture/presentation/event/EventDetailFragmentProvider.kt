package me.linhthengo.androiddddarchitechture.presentation.event

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface EventDetailFragmentProvider {
    @ContributesAndroidInjector
    fun provideEventDetailFragment(): EventDetailFragment
}