package com.example.easytravel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlin.jvm.java as java
import kotlinx.android.synthetic.main.activity_city_details.*

class CityDetails : AppCompatActivity() {
    private lateinit var myGoogleMap: GoogleMap
    private lateinit var myMapView: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_details)
        val city = intent.getParcelableExtra<City>(HomeActivity.USER_KEY)
        supportActionBar?.title = "Scopri: " + city.name
        val cityName = city.name

        collapsingToolbar_ID.title = "" + city?.region + ", " + city?.country + " "
        Picasso.get().load(city?.urlPhoto).into(image_collapsingToolbar)
        description_textView.text = city?.description

        map_button.setOnClickListener {
            val intentMap = Intent(this,MapsActivity::class.java)
            intentMap.putExtra("cityName",cityName)
            startActivity(intentMap)
        }

        fetchCities(city)

        meals_button.setOnClickListener{
            meals_scrollView.fullScroll(R.id.meals_placeHolder);
        }

        sites_button.setOnClickListener{
            sites_scrollView.fullScroll(R.id.sites_placeHolder);
        }


    }
    companion object{
        val ATTRIBUTE_KEY = "ATTRIBUTE_KEY"
    }

    private fun fetchCities(city: City) {
        val refSites = FirebaseDatabase.getInstance().getReference("/city/${city.name}/Tourist sites")
        refSites.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CityDetails,"ERROR:${error}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val touristSites = it.getValue(TouristSites::class.java)
                    if(touristSites!= null){
                        adapter.add(MytouristSitesAdapter(touristSites))
                    }
                }
                //Show Meals
                adapter.setOnItemClickListener{item, view ->
                    val mytouristSitesAdapter = item as MytouristSitesAdapter
                    val intent = Intent(view.context,SitesDetails::class.java)
                    intent.putExtra(ATTRIBUTE_KEY,mytouristSitesAdapter.touristSites)
                    intent.putExtra("cityName",city.name )
                    startActivity(intent)
                }
                touristSites_RecyclerView.adapter=adapter
            }

        })

        val refMeals = FirebaseDatabase.getInstance().getReference("/city/${city.name}/Typical Meals")
        refMeals.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CityDetails,"ERROR:${error}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                Log.d(CityDetails::class.java.name,snapshot.toString())
                snapshot.children.forEach{
                    val typicalMeal = it.getValue(TypicalMeal::class.java)
                    if(typicalMeal!= null){
                        adapter.add(MyMealsAdapter(typicalMeal))
                    }
                }
                //Click on items to see details
                adapter.setOnItemClickListener{item, view ->
                    val myMealsAdapter=item as MyMealsAdapter
                    val intent = Intent(view.context,MealDetails::class.java)
                    intent.putExtra(ATTRIBUTE_KEY,myMealsAdapter.typicalMeal)
                    intent.putExtra("cityName",city.name )
                    startActivity(intent)
                }
                meals_RecyclerView.adapter= adapter
            }

        })
    }
}
