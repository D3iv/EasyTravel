package com.example.easytravel

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException
import java.security.Permission
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList
import kotlin.random.Random

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object{
        const val LOCATION_PERMISSION_REQUEST_CODE=1
    }
    private lateinit var mMap: GoogleMap
    private var myLatitude : Double = 0.00
    private var myLongitude: Double = 0.00
    private lateinit var mylatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val cityName = intent.getStringExtra("cityName")
        Log.d(MapsActivity::class.java.name, "Città: $cityName")
        supportActionBar?.title = "Mappa di " + cityName
        val geocoder = Geocoder(this)
        var list = ArrayList<Address>()
        try{
            list = geocoder.getFromLocationName(cityName,1) as ArrayList<Address>
        }catch(e: IOException){

        }
        if(list.size >0){
            val address = list[0]
            Toast.makeText(this,cityName,Toast.LENGTH_LONG).show()

            myLatitude=address.latitude
            myLongitude=address.longitude
            mylatLng = LatLng(myLatitude,myLongitude)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(myLatitude,myLongitude,1)
        val address = addresses[0].getAddressLine(0)
        val city = addresses[0].locality
        mMap.addMarker(
            MarkerOptions().position(mylatLng).title("$city, $address")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatLng,10f))

        mMap.isMyLocationEnabled
        mMap.uiSettings.isZoomControlsEnabled
        mMap.uiSettings.isMapToolbarEnabled
        mMap.uiSettings.isCompassEnabled
        mMap.uiSettings.isZoomGesturesEnabled

    }
}