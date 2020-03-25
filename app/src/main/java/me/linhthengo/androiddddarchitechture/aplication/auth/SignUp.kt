package me.linhthengo.androiddddarchitechture.aplication.auth

import arrow.core.Either
import com.google.firebase.auth.FirebaseUser
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.UseCase
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import javax.inject.Inject

class SignUp @Inject constructor(private val firebaseAuthManager: FirebaseAuthManager) :
    UseCase<FirebaseUser, SignUp.Params>() {

    data class Params(val email: String, val password: String)

    override suspend fun run(params: Params): Either<Failure, FirebaseUser> =
        firebaseAuthManager.register(params.email, params.password)
}
