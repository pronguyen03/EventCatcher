package me.linhthengo.androiddddarchitechture.core.di

import dagger.Component
import me.linhthengo.androiddddarchitechture.AndroidApplication
import me.linhthengo.androiddddarchitechture.presentation.MainActivity
import me.linhthengo.androiddddarchitechture.presentation.SplashFragment
import me.linhthengo.androiddddarchitechture.presentation.auth.SignInFragment
import me.linhthengo.androiddddarchitechture.presentation.auth.SignUpFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
    fun inject(mainActivity: MainActivity)

    fun inject(splashFragment: SplashFragment)
    fun inject(signInFragment: SignInFragment)
    fun inject(signUpFragment: SignUpFragment)
}