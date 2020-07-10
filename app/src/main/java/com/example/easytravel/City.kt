package com.example.easytravel

import android.os.ParcelFileDescriptor
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class City(val name:String, val region: String, val urlPhoto: String, val country: String, val description: String): Parcelable {
    constructor(): this("","","","","")
}