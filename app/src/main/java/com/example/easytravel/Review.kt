package com.example.easytravel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Review(val comment:String,val uid:String, val nome:String): Parcelable {
    constructor(): this("","","")
}