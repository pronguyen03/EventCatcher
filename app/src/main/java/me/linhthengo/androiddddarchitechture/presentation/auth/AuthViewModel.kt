package me.linhthengo.androiddddarchitechture.presentation.auth

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.platform.BaseViewModel

abstract class AuthViewModel : BaseViewModel() {
    sealed class State {
        data class Failure(val message: String) : State()
        object Success : State()
        object Loading : State()
    }

    var state = MutableLiveData<State>()

    abstract fun handleFailure(failure: Failure)
    abstract fun handleSuccess(firebaseUser: FirebaseUser)

}
