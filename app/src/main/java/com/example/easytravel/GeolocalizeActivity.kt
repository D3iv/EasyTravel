package com.example.easytravel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class GeolocalizeActivity : AppCompatActivity(), OnMapReadyCallback {
    private var locationManager : LocationManager? = null
    private lateinit var mylatLng: LatLng
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geolocalize)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        try {
            // Request location updates
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0L,0f,locationListener)
        } catch(ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private val locationListener: LocationListener =
        LocationListener { location -> mylatLng= LatLng(location.latitude,location.longitude) }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.addMarker(
            MarkerOptions().position(mylatLng).title("")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatLng,10f))

        mMap.isMyLocationEnabled
        mMap.uiSettings.isZoomControlsEnabled
        mMap.uiSettings.isMapToolbarEnabled
        mMap.uiSettings.isCompassEnabled
        mMap.uiSettings.isZoomGesturesEnabled
    }
}
