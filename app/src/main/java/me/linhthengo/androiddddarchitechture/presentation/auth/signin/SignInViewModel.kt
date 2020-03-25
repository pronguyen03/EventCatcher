package me.linhthengo.androiddddarchitechture.presentation.auth.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.aplication.auth.SignIn
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.platform.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

class SignInViewModel @Inject constructor(val signIn: SignIn) : BaseViewModel() {
    sealed class State {
        data class SignInFailure(val message: String) : State()
        object SignInSuccess : State()
        object SignInLoading : State()
    }

    var state = MutableLiveData<State>()

    private fun handleFailure(failure: Failure) {
        Timber.tag(TAG).e(failure.message)
        this.state.postValue(State.SignInFailure(failure.message))
    }

    private fun handleSuccess(firebaseUser: FirebaseUser) {
        Timber.tag(TAG).d(firebaseUser.displayName)
        this.state.postValue(State.SignInSuccess)
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        state.postValue(State.SignInLoading)
        signIn(SignIn.Params(email, password), this) {
            it.fold(::handleFailure, ::handleSuccess)
        }
    }

    companion object {
        const val TAG = "SignInVM"
    }
}
