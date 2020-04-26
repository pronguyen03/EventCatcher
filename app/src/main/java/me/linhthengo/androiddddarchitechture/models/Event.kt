package me.linhthengo.androiddddarchitechture.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import me.linhthengo.androiddddarchitechture.enums.Category

@Parcelize
data class Event(
    var id: String = "",
    var name: String = "",
    var image: String = "https://danangfantasticity.com/wp-content/uploads/2018/03/da-nang-thanh-pho-cua-nhung-cay-cau-07.jpg",
    var description: String = "",
    var locationLat: Double = 0.0,
    var locationLng: Double = 0.0,
    var location: String = "",
    var locationName: String = "",
    var category: Category? = null,
    var startDate: Long = System.currentTimeMillis(),
    var endDate: Long? = null,
    var hostId: String? = "",
    var hostName: String? = "",
    var listInterest: MutableList<User> = mutableListOf(),
    var listParticipant: MutableList<User> = mutableListOf()
) : Parcelable