package me.linhthengo.androiddddarchitechture.domain.core

import arrow.core.Either

abstract class ValueObject<T>(open val value: Either<ValueFailures<T>, T>) {}