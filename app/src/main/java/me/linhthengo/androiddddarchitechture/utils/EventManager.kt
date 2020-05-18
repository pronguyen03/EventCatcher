package me.linhthengo.androiddddarchitechture.utils

import android.content.Context
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.enums.Category
import me.linhthengo.androiddddarchitechture.models.Event

//class EventManager(val db: FirebaseFirestore, val context: Context) {
//    suspend fun getListAllEvent(): Either<Failure, MutableList<Event>>  {
//        val listEvent = mutableListOf<Event>()
//        try {
//            val result = db.collection("event").get().await()
//            if (result != null && !result.isEmpty) {
//                for (document in result) {
//                    val event = Event()
//                    event.id = document.id
//                    event.name = document.data["name"].toString()
//                    event.image = document.data["image"].toString()
//                    event.description = document.data["description"].toString()
//                    event.locationLat = document.data["locationLat"] as Double
//                    event.locationLng = document.data["locationLng"] as Double
//                    event.location = document.data["location"].toString()
//                    event.locationName = document.data["locationName"].toString()
//                    event.category = document.data["category"] as Category
//                    event.startDate = document.data["startDate"] as Long
//                    event.endDate = document.data["endDate"] as Long
//                    event.hostId = document.data["hostId"].toString()
//                    event.hostName = document.data["hostName"].toString()
//                    listEvent.add(Event())
//                }
//                return Right(listEvent)
//            }
//            return Left(Failure.ServerError("Result: $result, User: ${result.query}"))
//        } catch (e: Exception) {
//            return Left(Failure.ServerError(e.message ?: e.toString()))
//        }
//
//    }
//}