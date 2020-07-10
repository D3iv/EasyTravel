package com.example.easytravel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_city_details.*

class MealDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_details)
        val typicalMeal = intent.getParcelableExtra<TypicalMeal>(CityDetails.ATTRIBUTE_KEY)
        supportActionBar?.title = "Scopri: " + typicalMeal.name
        collapsingToolbar_ID.title = ""
        Picasso.get().load(typicalMeal?.photoUrl).into(image_collapsingToolbar)
        description_textView.text = typicalMeal?.description

    }
}