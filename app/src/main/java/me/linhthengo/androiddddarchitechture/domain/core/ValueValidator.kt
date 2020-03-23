package me.linhthengo.androiddddarchitechture.domain.core

import android.util.Patterns.EMAIL_ADDRESS
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right

object ValueValidator {
    fun validateEmailAddress(input: String): Either<ValueFailures<String>, String> {
        return if (EMAIL_ADDRESS.matcher(input).matches()) {
            Right(input)
        } else {
            Left(ValueFailures.InvalidEmail(failureValue = input))
        }
    }

    fun validatePassword(input: String): Either<ValueFailures<String>, String> {
        return if (input.length >= 6) {
            Right(input)
        } else {
            Left(ValueFailures.ShortPassword(failureValue = input))
        }
    }
}
