package com.example.easytravel

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_city_details.*

class CityDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_details)
        val city = intent.getParcelableExtra<City>(HomeActivity.USER_KEY)
        supportActionBar?.title= "Scopri: "+city.name
        collapsingToolbar_ID.title = "" + city?.region + ", " + city?.country + " "
        description_textView.text = city?.desc
        Picasso.get().load(city?.urlPhoto).into(image_collapsingToolbar)
    }
}
