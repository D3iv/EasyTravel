package com.example.easytravel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TouristSites(val photoUrl:String,val description: String, val name:String) : Parcelable {
    constructor():this("","","")
}