package me.linhthengo.androiddddarchitechture.aplication.auth

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.UseCase
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import javax.inject.Inject

class GetGoogleSignInClient @Inject constructor(private val firebaseAuthManager: FirebaseAuthManager) :
    UseCase<GoogleSignInClient, Unit>() {
    override suspend fun run(params: Unit): Either<Failure, GoogleSignInClient> {
        return try {
            Right(firebaseAuthManager.googleSignInClient)
        } catch (e: Exception) {
            Left(Failure.Unknown)
        }
    }
}