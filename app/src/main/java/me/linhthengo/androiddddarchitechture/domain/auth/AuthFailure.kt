package me.linhthengo.androiddddarchitechture.domain.auth

sealed class AuthFailure {

    object CancelByUser : AuthFailure()

    object ServerError : AuthFailure()

    object EmailAlreadyUse : AuthFailure()

    object InvalidEmailAndPasswordCombination : AuthFailure()
}