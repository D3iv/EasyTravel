package com.example.easytravel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.jvm.java as java

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val uid = FirebaseAuth.getInstance().uid

        if(uid == null){
            val intentReg = Intent(this,RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentReg)
        }

        list_button.setOnClickListener {
            fetchData()
        }
        search_button.setOnClickListener {
            if(search_editText.text != null) {
                val cityName = search_editText.text.toString()
                val ref = FirebaseDatabase.getInstance()
                    .getReference("/city")
                    .orderByChild("name").equalTo(cityName)

                ref.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@HomeActivity,"Nothing found",Toast.LENGTH_LONG).show()
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val adapter = GroupAdapter<ViewHolder>()
                        //new entry for city
                        snapshot.children.forEach{
                            val city = it.getValue(City::class.java)
                            if(city != null) {
                                adapter.add(MyAdapter(city))
                            }else{
                                fetchRegion(cityName)
                            }
                        }
                        //Click on items to see details
                        adapter.setOnItemClickListener{item, view ->
                            val myAdapter = item as MyAdapter
                            val intent = Intent(view.context,CityDetails::class.java)
                            intent.putExtra(USER_KEY,myAdapter.city)
                            startActivity(intent)
                        }
                        listView_recyclerView.adapter= adapter
                    }
                })
            }
        }

        fetchData()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intentReg = Intent(this, RegistrationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentReg)
            }
            R.id.account_option -> {
                val intentOptions = Intent(this, AccountActivity::class.java)
                startActivity(intentOptions)
            }
            R.id.about_option -> {
                val intentAbout = Intent(this, AboutActivity::class.java)
                startActivity(intentAbout)
            }
            R.id.geolocal_button ->{
                val intentLocalize = Intent(this,GeolocalizeActivity::class.java)
                startActivity(intentLocalize)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchData(){
        val ref = FirebaseDatabase.getInstance().getReference("/city")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,"ERROR:${error}",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                //new entry for city
                snapshot.children.forEach{
                    val city = it.getValue(City::class.java)
                    if(city != null) {
                        adapter.add(MyAdapter(city))
                    }
                }
                //Click on items to see details
                adapter.setOnItemClickListener{item, view ->
                    val myAdapter = item as MyAdapter
                    val intent = Intent(view.context,CityDetails::class.java)
                    intent.putExtra(USER_KEY,myAdapter.city)
                    startActivity(intent)
                }
                listView_recyclerView.adapter= adapter
            }

        })
    }

    private fun searchCity(text: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/city").child("$text").equalTo(text)
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,"Oops..probably you insert an unkown value",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                Log.d(HomeActivity::class.java.name,snapshot.toString())
                //new entry for city
                snapshot.children.forEach{
                    val city = it.getValue(City::class.java)
                    if(city != null) {
                        adapter.add(MyAdapter(city))
                    }
                }
                //Click on items to see details
                adapter.setOnItemClickListener{item, view ->
                    val myAdapter = item as MyAdapter
                    val intent = Intent(view.context,CityDetails::class.java)
                    intent.putExtra(USER_KEY,myAdapter.city)
                    startActivity(intent)
                }
                listView_recyclerView.adapter= adapter
            }

        })
    }

    private fun fetchRegion(region: String){
        val refRegion = FirebaseDatabase.getInstance()
            .getReference("/city")
            .orderByChild("region").equalTo(region)
        Toast.makeText(this,"$region",Toast.LENGTH_LONG).show()

        refRegion.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,"Nothing found",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                //new entry for city
                snapshot.children.forEach{
                    val city = it.getValue(City::class.java)
                    if(city != null) {
                        adapter.add(MyAdapter(city))
                    }else{
                        fetchCountry(region)
                    }
                }
                //Click on items to see details
                adapter.setOnItemClickListener{item, view ->
                    val myAdapter = item as MyAdapter
                    val intent = Intent(view.context,CityDetails::class.java)
                    intent.putExtra(USER_KEY,myAdapter.city)
                    startActivity(intent)
                }
                listView_recyclerView.adapter= adapter
            }
        })
    }

    private fun fetchCountry(country: String){
        val refCountry = FirebaseDatabase.getInstance()
            .getReference("/city")
            .orderByChild("country").equalTo(country)

        refCountry.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,"Nothing found",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                //new entry for city
                snapshot.children.forEach{
                    val city = it.getValue(City::class.java)
                    if(city != null) {
                        adapter.add(MyAdapter(city))
                    }
                }
                //Click on items to see details
                adapter.setOnItemClickListener{item, view ->
                    val myAdapter = item as MyAdapter
                    val intent = Intent(view.context,CityDetails::class.java)
                    intent.putExtra(USER_KEY,myAdapter.city)
                    startActivity(intent)
                }
                listView_recyclerView.adapter= adapter
            }
        })


    }
}