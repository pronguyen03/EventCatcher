package me.linhthengo.androiddddarchitechture.aplication.home

import arrow.core.Either
import arrow.core.Right
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.UseCase
import me.linhthengo.androiddddarchitechture.models.Event
import javax.inject.Inject

//class GetListAllEvent @Inject constructor(private val eventManager: EventManager) : UseCase<MutableList<Event>, Unit?>() {
//    override suspend fun run(params: Unit?): Either<Failure, MutableList<Event>> = eventManager.getListAllEvent()
//}