package me.linhthengo.androiddddarchitechture.utils

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import me.linhthengo.androiddddarchitechture.core.Failure

class FirebaseAuthManager(val auth: FirebaseAuth) {

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
}

