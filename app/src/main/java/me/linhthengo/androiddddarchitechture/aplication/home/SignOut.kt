package me.linhthengo.androiddddarchitechture.aplication.home

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.UseCase
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import javax.inject.Inject

class SignOut @Inject constructor(private val firebaseAuthManager: FirebaseAuthManager) : UseCase<Unit, Unit?>(){
    override suspend fun run(params: Unit?): Either<Failure, Unit> {
        return try {
            firebaseAuthManager.signOut()
            Right(Unit)
        } catch (e: Exception) {
            Left(Failure.Unknown)
        }
    }

}