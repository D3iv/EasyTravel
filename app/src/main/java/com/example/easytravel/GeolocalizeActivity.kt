package com.example.easytravel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
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

    private lateinit var mylatLng: LatLng
    private var myLatitude: Double = 0.00
    private var myLongitude:Double = 0.00
    private var PERMISSION_ID = 100
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geolocalize)

        //init the fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //call the geolocation function
        if(checkPermission()){
            if(isPositionEnabled()){
                Log.d(GeolocalizeActivity::class.java.name,"Fetching position (1)")
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{task ->
                    var location = task.result
                    Log.d(GeolocalizeActivity::class.java.name,"$location")
                    if(location == null){
                        Log.d(GeolocalizeActivity::class.java.name,"Fetching position (2)")
                        locationRequest = LocationRequest()
                        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
                        locationRequest.interval=0
                        locationRequest.numUpdates= 2
                        if (checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        )
                            fusedLocationProviderClient!!.requestLocationUpdates(
                                locationRequest,locationCallBack,Looper.myLooper()
                            )
                    }else{
                        Log.d(GeolocalizeActivity::class.java.name,"Fetching position (3)")
                        myLatitude=location.latitude
                        myLongitude=location.longitude
                        mylatLng = LatLng(myLatitude,myLongitude)

                    }

                }
            }else{
                Toast.makeText(this@GeolocalizeActivity,"permission acquired",Toast.LENGTH_LONG).show()
            }
        }else{
            RequestPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        getAddress()
        mMap = googleMap
        mMap.addMarker(
            MarkerOptions().position(mylatLng).title(getAddress())
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatLng,10f))

        mMap.isMyLocationEnabled
        mMap.uiSettings.isZoomControlsEnabled
        mMap.uiSettings.isMapToolbarEnabled
        mMap.uiSettings.isCompassEnabled
        mMap.uiSettings.isZoomGesturesEnabled
    }

    private fun checkPermission():Boolean{
        return checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                ||
                checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun RequestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID)
    }

    private fun isPositionEnabled():Boolean{
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    /*

     */
    private fun getAddress():String{
        var geocoder = Geocoder(this, Locale.getDefault())
        var address= geocoder.getFromLocation(myLatitude,myLongitude,1)
        Log.d(GeolocalizeActivity::class.java.name,"Fetching position (5)")
        val cityName= address[0].locality
        val countryName = address[0].countryName
        return "$cityName, $countryName"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@GeolocalizeActivity,"permission acquired",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private val locationCallBack = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            Log.d(GeolocalizeActivity::class.java.name,"Fetching position (4)")
            var lastLocation=p0.lastLocation
            myLatitude=lastLocation.latitude
            myLongitude=lastLocation.longitude
            mylatLng = LatLng(myLatitude,myLongitude)
        }
    }
}