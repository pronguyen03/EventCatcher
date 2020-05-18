package me.linhthengo.androiddddarchitechture.presentation.auth.yourevent

import com.google.firebase.auth.FirebaseUser
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.presentation.auth.AuthViewModel
import me.linhthengo.androiddddarchitechture.presentation.auth.signin.SignInViewModel.Companion.TAG
import timber.log.Timber
import javax.inject.Inject

class YourEventFragmentViewModel @Inject constructor(): AuthViewModel(){
    override fun handleFailure(failure: Failure) {
        Timber.tag(TAG).e(failure.message)
        this.state.postValue(State.Failure(failure.message))
    }

    override fun handleSuccess(firebaseUser: FirebaseUser) {
        Timber.tag(TAG).d(firebaseUser.displayName)
        this.state.postValue(State.Success)    }
}