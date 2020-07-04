package com.example.easytravel

class User(val uid:String,val username:String, val profileImageUrl:String, val email:String) {
    constructor(): this("","","", "")

}