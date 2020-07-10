package com.example.easytravel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TypicalMeal(val name:String,val description:String,val photoUrl:String) : Parcelable {
    constructor(): this("","","")
}