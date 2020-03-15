package me.linhthengo.androiddddarchitechture.domain.auth

import me.linhthengo.androiddddarchitechture.domain.core.ValueObject
import me.linhthengo.androiddddarchitechture.domain.core.ValueValidator

data class EmailAddress(val input: String) :
    ValueObject<String>(ValueValidator.validateEmailAddress(input))

data class Password(val input: String) : ValueObject<String>(ValueValidator.validatePassword(input))
