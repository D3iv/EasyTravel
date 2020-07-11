package com.example.easytravel

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_city_details.*
import java.io.IOException

class SitesDetails : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    private lateinit var mMap: GoogleMap
    private var myLatitude : Double = 0.00
    private var myLongitude: Double = 0.00
    private lateinit var mylatLng: LatLng
    private lateinit var myAddress: Address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sites_details)
        val site = intent.getParcelableExtra<TouristSites>(CityDetails.ATTRIBUTE_KEY)
        val cityName=intent.getStringExtra("cityName")
        supportActionBar?.title = "Scopri: " + site.name
        collapsingToolbar_ID.title = ""
        Picasso.get().load(site?.photoUrl).into(image_collapsingToolbar)
        description_textView.text = site?.description

        val geocoder = Geocoder(this)
        var list = ArrayList<Address>()
        try{
            list = geocoder.getFromLocationName(cityName+site.name,5) as ArrayList<Address>
        }catch(e: IOException){

        }
        if(list.size >0){
            val address = list[0]
            myAddress = list[0]
            myLatitude = address.latitude
            myLongitude = address.longitude
            mylatLng = LatLng(myLatitude, myLongitude)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            mMap = p0
        }
        mMap.addMarker(
            MarkerOptions().position(mylatLng).title(myAddress.getAddressLine(0).toString())
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatLng,15f))

        mMap.isMyLocationEnabled
        mMap.uiSettings.isZoomControlsEnabled
        mMap.uiSettings.isMapToolbarEnabled
        mMap.uiSettings.isCompassEnabled
        mMap.uiSettings.isZoomGesturesEnabled
    }
}