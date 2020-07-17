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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.util.*

class GeolocalizeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mCurrentLocation : Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE_LOCATION_PERIMISSION = 1
    private lateinit var mylatLng: LatLng
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.localization_info_layout)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLastLocation()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    }

    private fun fetchLastLocation() {
        if (checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE_LOCATION_PERIMISSION)
            return
        }
        val task: Task<Location> =
            mFusedLocationProviderClient.lastLocation
        task.addOnSuccessListener(object: OnSuccessListener<Location>{
            override fun onSuccess(p0: Location?) {
               if(p0!= null){
                   mCurrentLocation = p0
                   Log.d("GeolocalizeActivity","ACTUAL POSITION: $mylatLng")
                   Toast.makeText(this@GeolocalizeActivity,"position found",Toast.LENGTH_LONG).show()
                   val mapFragment = supportFragmentManager
                       .findFragmentById(R.id.clientLocation_map) as SupportMapFragment
                   mapFragment.getMapAsync(this@GeolocalizeActivity)
               }
            }

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_LOCATION_PERIMISSION){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                fetchLastLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap){
        mMap = googleMap
        val latLang = LatLng(mCurrentLocation.latitude,mCurrentLocation.longitude)
        Log.d(GeolocalizeActivity::class.java.name,"ACTUAL POSITION: $mylatLng")
        mMap.addMarker(
            MarkerOptions().position(latLang).title("")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatLng,10f))

        mMap.isMyLocationEnabled
        mMap.uiSettings.isZoomControlsEnabled
        mMap.uiSettings.isMapToolbarEnabled
        mMap.uiSettings.isCompassEnabled
        mMap.uiSettings.isZoomGesturesEnabled
    }
}
