package me.linhthengo.androiddddarchitechture.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.aplication.home.SignOut
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.platform.BaseViewModel
import me.linhthengo.androiddddarchitechture.presentation.auth.signin.SignInViewModel
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val signOut: SignOut) : BaseViewModel() {
    sealed class State {
        data class Failure(val message: String) : State()
        object Success : State()
    }

    var state = MutableLiveData<State>()

    fun signOut() = viewModelScope.launch {
        signOut(null,this) {
            it.fold(::handleFailure, ::handleSuccess)
        }
    }

    private fun handleFailure(failure: Failure) {
        Timber.tag(SignInViewModel.TAG).e(failure.message)
    }

    private fun handleSuccess(unit: Unit) {
        this.state.postValue(State.Success)
    }

}