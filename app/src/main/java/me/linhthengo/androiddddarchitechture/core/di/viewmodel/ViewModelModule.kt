package me.linhthengo.androiddddarchitechture.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.linhthengo.androiddddarchitechture.presentation.auth.signin.SignInViewModel
import me.linhthengo.androiddddarchitechture.presentation.auth.signup.SignUpViewModel
import me.linhthengo.androiddddarchitechture.presentation.home.HomeViewModel
import me.linhthengo.androiddddarchitechture.presentation.home.ProfileViewModel
import me.linhthengo.androiddddarchitechture.presentation.home.TutorialViewModel
import me.linhthengo.androiddddarchitechture.presentation.home.YourEventViewModel

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    internal abstract fun signInViewModel(viewModel: SignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    internal abstract fun signUpViewModel(viewModel: SignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    internal abstract fun profileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TutorialViewModel::class)
    internal abstract fun tutorialViewModel(viewModel: TutorialViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(YourEventViewModel::class)
    internal abstract fun yourEventViewModel(viewModel: YourEventViewModel): ViewModel
    //Add more ViewModels here
}
