package me.linhthengo.androiddddarchitechture.domain.core

sealed class ValueFailures<T> {
    data class InvalidEmail<T>(val failureValue: String) : ValueFailures<T>()
    data class ShortPassword<T>(val failureValue: String) : ValueFailures<T>()
    data class NotTheSamePassword<T>(val failureValue: String) : ValueFailures<T>()
    data class Empty<T>(val failureValue: String) : ValueFailures<T>()
}
