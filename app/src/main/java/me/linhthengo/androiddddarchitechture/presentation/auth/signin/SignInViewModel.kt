package me.linhthengo.androiddddarchitechture.presentation.auth.signin

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.aplication.auth.SignIn
import me.linhthengo.androiddddarchitechture.core.platform.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

class SignInViewModel @Inject constructor(val signIn: SignIn) : BaseViewModel() {
    fun signIn(email: String, password: String, onSuccess: () -> Unit) = viewModelScope.launch {
        signIn(SignIn.Params(email, password), this) {
            it.fold(::handleFailure) { firebaseUser ->
                Timber.tag(TAG).d(firebaseUser.displayName)
                onSuccess()
            }
        }
    }

    companion object {
        const val TAG = "SignInVM"
    }
}
