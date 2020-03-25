package me.linhthengo.androiddddarchitechture.presentation.auth.signup

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.aplication.auth.SignUp
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.presentation.auth.AuthViewModel
import timber.log.Timber
import javax.inject.Inject

class SignUpViewModel @Inject constructor(private val signUp: SignUp) : AuthViewModel() {

    override fun handleFailure(failure: Failure) {
        Timber.tag(TAG).e(failure.message)
        this.state.postValue(State.Failure(failure.message))
    }

    override fun handleSuccess(firebaseUser: FirebaseUser) {
        Timber.tag(TAG).d(firebaseUser.displayName)
        this.state.postValue(State.Success)
    }

    fun signUp(email: String, password: String) = viewModelScope.launch {
        state.postValue(State.Loading)
        signUp(SignUp.Params(email, password), this) { it.fold(::handleFailure, ::handleSuccess) }
    }

    companion object {
        const val TAG = "SignUpVM"
    }
}
