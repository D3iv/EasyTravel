package com.example.easytravel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.*
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.localization_info_layout.*

class GeolocalizeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mCurrentLocation : Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE_LOCATION_PERIMISSION = 1
    private lateinit var mMap: GoogleMap
    private lateinit var mLatLang: LatLng
    private lateinit var mCity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.localization_info_layout)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLastLocation()

        cityLink_textView.setOnClickListener { fetchCityFromDB(mCity) }
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

        val task: Task<Location> = mFusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { p0 ->
            if(p0!= null){
                Log.d(GeolocalizeActivity::class.java.name,"$p0")
                mCurrentLocation = p0
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this@GeolocalizeActivity)
            }
        }
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
        mLatLang = LatLng(mCurrentLocation.latitude,mCurrentLocation.longitude)
        Toast.makeText(this@GeolocalizeActivity,"position found",Toast.LENGTH_LONG).show()
        val geocoder = Geocoder(this@GeolocalizeActivity)
        val addresses = geocoder.getFromLocation(mCurrentLocation.latitude,mCurrentLocation.longitude,1)
        val address = addresses[0].getAddressLine(0)
        mCity = addresses[0].locality
        cityLink_placeHolder.text = "Ti trovi qui: $mCity"
        mMap.addMarker(
            MarkerOptions().position(mLatLang).title("$mCity, $address")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLang,15f))

        mMap.isMyLocationEnabled
        mMap.uiSettings.isZoomControlsEnabled
        mMap.uiSettings.isMapToolbarEnabled
        mMap.uiSettings.isCompassEnabled
        mMap.uiSettings.isZoomGesturesEnabled
    }

    private fun fetchCityFromDB(mCity: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/city/$mCity")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GeolocalizeActivity,"ERROR:${error}",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //new entry for city
                    val city = snapshot.getValue(City::class.java)
                    if(city != null){
                        doIntent(city)
                    }else{
                        Toast.makeText(this@GeolocalizeActivity,"Nessuna informazione", Toast.LENGTH_LONG).show()
                        finish()
                    }
            }

        })
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun doIntent(city: City) {
        val intent = Intent(this,CityDetails::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(USER_KEY,city)
        startActivity(intent)
        finish()
    }
}
