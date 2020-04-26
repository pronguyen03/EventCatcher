package me.linhthengo.androiddddarchitechture.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var uid: String = "",
    var name: String? = "",
    var email: String? = ""
) : Parcelable