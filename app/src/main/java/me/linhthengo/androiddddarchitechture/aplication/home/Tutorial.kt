package me.linhthengo.androiddddarchitechture.aplication.home

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.UseCase
import javax.inject.Inject

class Tutorial @Inject constructor() : UseCase<Unit, Unit?>() {
    override suspend fun run(params: Unit?): Either<Failure, Unit> {
        return try {
            //firebaseAuthManager.signOut()
            Right(Unit)
        } catch (e: Exception) {
            Left(Failure.Unknown)
        }
    }
}