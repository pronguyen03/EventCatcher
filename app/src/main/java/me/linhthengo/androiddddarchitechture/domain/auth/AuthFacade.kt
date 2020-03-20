package me.linhthengo.androiddddarchitechture.domain.auth

import arrow.core.Either

interface AuthFacade {
    suspend fun registerWithEmailAndPassword(
        emailAddress: EmailAddress,
        password: Password
    ): Either<AuthFailure, Unit>

    suspend fun signInWithEmailAndPassword(
        emailAddress: EmailAddress,
        password: Password
    ): Either<AuthFailure, Unit>

    suspend fun signInWithGoogle(): Either<AuthFailure, Unit>
}