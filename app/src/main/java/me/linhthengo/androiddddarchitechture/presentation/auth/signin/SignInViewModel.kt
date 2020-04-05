package me.linhthengo.androiddddarchitechture.presentation.auth.signin

import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.linhthengo.androiddddarchitechture.aplication.auth.GetGoogleSignInClient
import me.linhthengo.androiddddarchitechture.aplication.auth.SignIn
import me.linhthengo.androiddddarchitechture.aplication.auth.SignInWithGoogle
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.presentation.auth.AuthViewModel
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import timber.log.Timber
import javax.inject.Inject


class SignInViewModel @Inject constructor(
    private val signIn: SignIn,
    private val signInWithGoogle: SignInWithGoogle,
    private val getGoogleSignInClient: GetGoogleSignInClient,
    private val firebaseAuthManager: FirebaseAuthManager
) : AuthViewModel() {

    override fun handleFailure(failure: Failure) {
        Timber.tag(TAG).e(failure.message)
        this.state.postValue(State.Failure(failure.message))
    }

    override fun handleSuccess(firebaseUser: FirebaseUser) {
        Timber.tag(TAG).d(firebaseUser.displayName)
        this.state.postValue(State.Success)
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        state.postValue(State.Loading)
        signIn(SignIn.Params(email, password), this) {
            it.fold(::handleFailure, ::handleSuccess)
        }
    }

    fun signInWithGoogle(googleSignInAccount: GoogleSignInAccount) = viewModelScope.launch {
        val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        signInWithGoogle(SignInWithGoogle.Params(credential), this) {
            it.fold(::handleFailure, ::handleSuccess)
        }
    }

    fun getGoogleSignInClient() : GoogleSignInClient {
        return firebaseAuthManager.googleSignInClient;
    }

    fun getSignedInAccountFromIntent(data: Intent?) = viewModelScope.async {
        GoogleSignIn.getSignedInAccountFromIntent(data).await()
    }

    companion object {
        const val TAG = "SignInVM"
        const val GOOGLE_SIGN_IN_REQUEST_CODE = 0
    }
}
