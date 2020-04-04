package me.linhthengo.androiddddarchitechture.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.linhthengo.androiddddarchitechture.presentation.auth.signin.SignInViewModel
import me.linhthengo.androiddddarchitechture.presentation.auth.signup.SignUpViewModel
import me.linhthengo.androiddddarchitechture.presentation.home.HomeViewModel

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
    //Add more ViewModels here
}
