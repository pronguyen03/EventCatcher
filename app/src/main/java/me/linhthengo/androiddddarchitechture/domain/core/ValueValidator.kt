package me.linhthengo.androiddddarchitechture.domain.core

import arrow.core.Either

object ValueValidator {
    fun validateEmailAddress(input: String): Either<ValueFailures<String>, String> {
        val regex = """
            /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))${'$'}/
        """.trimIndent().toRegex()
        return if (regex.matches(input)) {
            Either.right(input)
        } else {
            Either.left(ValueFailures.InvalidEmail(failureValue = input))
        }
    }

    fun validatePassword(input: String): Either<ValueFailures<String>, String> {
        return if (input.length >= 6) {
            Either.right(input)
        } else {
            Either.left(ValueFailures.ShortPassword(failureValue = input))
        }
    }
}
