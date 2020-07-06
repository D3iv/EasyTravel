package com.example.easytravel

class City(val name:String, val urlPhoto: String, val tourist_sites:Array<String>, val typical_meals: Array<String>) {
    constructor(): this("","",arrayOf<String>(), arrayOf<String>())
}