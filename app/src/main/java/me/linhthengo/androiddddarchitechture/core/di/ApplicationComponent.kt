package me.linhthengo.androiddddarchitechture.core.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import me.linhthengo.androiddddarchitechture.AndroidApplication
import me.linhthengo.androiddddarchitechture.core.di.viewmodel.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ActivityBuilder::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<AndroidApplication>