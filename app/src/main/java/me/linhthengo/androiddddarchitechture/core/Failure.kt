package me.linhthengo.androiddddarchitechture.core

import me.linhthengo.androiddddarchitechture.core.Failure.FeatureFailure
import timber.log.Timber

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure(open val message: String) {
    init {
        Timber.tag(this::class.java.name).e(message)
    }

    data class NetworkConnection(override val message: String) : Failure(message)
    data class ServerError(override val message: String) : Failure(message)
    object Unknown : Failure("Unknown")

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure(message: String) : Failure(message)
}