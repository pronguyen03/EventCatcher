package me.linhthengo.androiddddarchitechture.utils

import android.content.Context
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.Failure

class FirebaseAuthManager(val auth: FirebaseAuth, context: Context) {
     private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    fun isLogin() = getCurrentUser() != null

    fun getCurrentUser() = auth.currentUser

    suspend fun getUpdateUser(user: FirebaseUser): Either<Failure, Unit> {
        return try {
            auth.updateCurrentUser(user).await()
            Right(Unit)
        } catch (e: Exception) {
            makeExceptionFailure(e)
        }
    }

    fun signOut() = auth.signOut()

    suspend fun register(email: String, password: String): Either<Failure, FirebaseUser> {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result != null && result.user != null) {
                return Right(result.user!!)
            }
            return makeAuthFailure(result)
        } catch (e: Exception) {
            return makeExceptionFailure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Either<Failure, FirebaseUser> {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result != null && result.user != null) {
                return Right(result.user!!)
            }
            return makeAuthFailure(result)
        } catch (e: Exception) {
            return makeExceptionFailure(e)
        }
    }

    private fun makeAuthFailure(result: AuthResult) =
        Left(Failure.ServerError("Result: $result, User: ${result.user}"))

    private fun makeExceptionFailure(e: Exception) =
        Left(Failure.ServerError(e.message ?: e.toString()))

    suspend fun signInWithGoogle(credential: AuthCredential) : Either<Failure, FirebaseUser> {
        try {
            val result = auth.signInWithCredential(credential).await()
            if (result != null && result.user != null) {
                return Right(result.user!!)
            }
            return makeAuthFailure(result)
        } catch (e: Exception) {
            return makeExceptionFailure(e)
        }
    }
}

