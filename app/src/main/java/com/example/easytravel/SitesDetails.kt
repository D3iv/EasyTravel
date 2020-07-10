package com.example.easytravel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_city_details.*

class SitesDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sites_details)
        val site = intent.getParcelableExtra<TouristSites>(CityDetails.ATTRIBUTE_KEY)
        supportActionBar?.title = "Scopri: " + site.name
        collapsingToolbar_ID.title = ""
        Picasso.get().load(site?.photoUrl).into(image_collapsingToolbar)
        description_textView.text = site?.description
    }
}