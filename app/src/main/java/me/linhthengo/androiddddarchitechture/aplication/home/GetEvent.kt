package me.linhthengo.androiddddarchitechture.aplication.home

import android.location.Location
import arrow.core.Either
import arrow.core.Right
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.UseCase
import me.linhthengo.androiddddarchitechture.models.Event
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import java.util.*
import javax.inject.Inject

class GetEvent @Inject constructor(private val firebaseAuthManager: FirebaseAuthManager):
    UseCase<MutableList<Event>, GetEvent.Params>() {

    data class Params(
        val isUpcoming: Boolean,
        val isOngoing: Boolean,
        val location: Location,
        val scope: Int,
        val startDate: Calendar)

    override suspend fun run(params: Params): Either<Failure, MutableList<Event>> {
        TODO()
    }
}