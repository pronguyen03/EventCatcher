package me.linhthengo.androiddddarchitechture.aplication.auth

import arrow.core.Either
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.UseCase
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import javax.inject.Inject

class SignInWithGoogle @Inject constructor(private val firebaseAuthManager: FirebaseAuthManager) :
    UseCase<FirebaseUser, SignInWithGoogle.Params>() {
    override suspend fun run(params: Params): Either<Failure, FirebaseUser> {
        return firebaseAuthManager.signInWithGoogle(params.credential)
    }

    data class Params(val credential: AuthCredential)
}