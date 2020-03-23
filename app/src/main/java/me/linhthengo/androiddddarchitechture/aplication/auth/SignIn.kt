package me.linhthengo.androiddddarchitechture.aplication.auth

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.UseCase
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import javax.inject.Inject

class SignIn @Inject constructor(private val firebaseAuthManager: FirebaseAuthManager) :
    UseCase<FirebaseUser, SignIn.Params>() {

    data class Params(val email: String, val password: String)

    override suspend fun run(params: Params): Either<Failure, FirebaseUser> {
        return try {
            val result = firebaseAuthManager.signInWithEmail(params.email, params.password).await()

            if (result.user != null) {
                return Right(result.user!!)
            }
            return Left(Failure.ServerError)
        } catch (e: Exception) {
            Left(Failure.ServerError)
        }
    }
}
