package me.linhthengo.androiddddarchitechture.presentation.auth.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.aplication.auth.SignUp
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.platform.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

class SignUpViewModel @Inject constructor(val signUp: SignUp) : BaseViewModel() {

    val state = MutableLiveData<State>()

    sealed class State {
        data class SignUpFailure(val message: String) : State()
        object SignUpSuccess : State()
        object SignUpLoading : State()
    }

    private fun handleFailure(failure: Failure) {
        Timber.tag(TAG).e(failure.message)
        this.state.postValue(State.SignUpFailure(failure.message))
    }

    private fun handleSuccess(firebaseUser: FirebaseUser) {
        Timber.tag(TAG).d(firebaseUser.displayName)
        this.state.postValue(State.SignUpSuccess)
    }


    fun signUp(email: String, password: String) = viewModelScope.launch {
        state.postValue(State.SignUpLoading)
        signUp(SignUp.Params(email, password), this) { it.fold(::handleFailure, ::handleSuccess) }
    }

    companion object {
        const val TAG = "SignUpVM"
    }
}