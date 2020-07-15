package com.example.easytravel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Review(val comment:String?,val nome:String?,val username:String?, val photoUrl:String?): Parcelable {
    constructor(): this("","","","")
}