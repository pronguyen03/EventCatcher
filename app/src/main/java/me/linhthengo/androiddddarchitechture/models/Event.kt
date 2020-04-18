package me.linhthengo.androiddddarchitechture.models

import com.google.android.gms.maps.model.LatLng
import me.linhthengo.androiddddarchitechture.enums.Category
import java.util.*


data class Event(
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    var locationLat: Double = 0.0,
    var locationLng: Double = 0.0,
    var location: String? = "",
    var category: Category? = null,
    var startDate: Long = System.currentTimeMillis(),
    var endDate: Long? = null,
    var hostId: String? = "",
    var hostName: String? = ""
)