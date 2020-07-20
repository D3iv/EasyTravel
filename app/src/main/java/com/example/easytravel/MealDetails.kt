package com.example.easytravel

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_city_details.collapsingToolbar_ID
import kotlinx.android.synthetic.main.activity_city_details.description_textView
import kotlinx.android.synthetic.main.activity_city_details.image_collapsingToolbar
import kotlinx.android.synthetic.main.activity_meal_details.*
import java.io.IOException

class MealDetails : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    private lateinit var mMap: GoogleMap
    private lateinit var myAddressList: ArrayList<Address>
    private lateinit var myAddress: Address
    private lateinit var mylatLng: LatLng
    private lateinit var cityName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_details)
        val typicalMeal = intent.getParcelableExtra<TypicalMeal>(CityDetails.ATTRIBUTE_KEY)
        cityName=intent.getStringExtra("cityName")
        supportActionBar?.title = "Scopri: " + typicalMeal.name

        collapsingToolbar_ID.title = ""
        Picasso.get().load(typicalMeal?.photoUrl).into(image_collapsingToolbar)
        description_textView.text = typicalMeal?.description

        val geocoder = Geocoder(this@MealDetails)
        myAddressList = ArrayList<Address>()
        try{
            myAddressList = geocoder.getFromLocationName(cityName+typicalMeal.name,1) as ArrayList<Address>
            Log.d(MealDetails::class.java.name, "Indirizzo: $myAddressList")
        }catch(e: IOException){
                Log.d("MealDetails",e.toString())
        }
        if(myAddressList.size >0){
                myAddress = myAddressList[0]
                mylatLng = LatLng(myAddress.latitude,myAddress.longitude)
        }
        Log.d("MealDetails","$cityName,${typicalMeal.name}")
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fetchReviews(typicalMeal.name)

        rate_button.setOnClickListener {
            rating_scrollView.fullScroll(R.id.rating_placeHolder)
        }

        comment_Button.setOnClickListener{
            val intentComment = Intent(this,CommentMealsActivity::class.java)
            intentComment.putExtra("cityName",cityName)
            intentComment.putExtra("mName",typicalMeal.name)
            startActivity(intentComment)
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            mMap = p0
        }
        addMarker(myAddress,mylatLng)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatLng,15f))

        mMap.isMyLocationEnabled
        mMap.uiSettings.isZoomControlsEnabled
        mMap.uiSettings.isMapToolbarEnabled
        mMap.uiSettings.isCompassEnabled
        mMap.uiSettings.isZoomGesturesEnabled

    }

    private fun addMarker(address: Address, latLng: LatLng) {
        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(address.getAddressLine(0).toString()))
    }

    private fun fetchReviews(name: String) {
        val refReviews = FirebaseDatabase.getInstance()
            .reference.child("/review").orderByChild("nome").equalTo(name)
        refReviews.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(MealDetails::class.java.name, "$snapshot")
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach {
                    val review = it.getValue(Review::class.java)
                    if (review != null) {
                        adapter.add(MyReviewAdapter(review))
                    }
                }
                rating_RecyclerView.adapter=adapter
            }
        })
    }

}