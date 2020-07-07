package com.example.easytravel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.list_layout.*
import kotlinx.android.synthetic.main.list_layout.view.*
import kotlin.collections.ArrayList
import kotlin.jvm.java as java

class HomeActivity : AppCompatActivity() {

    lateinit var mySearchText: EditText
    lateinit var myRecyclerView: RecyclerView

    lateinit var myDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        myDatabase= FirebaseDatabase.getInstance().getReference("cities")

        myRecyclerView.setHasFixedSize(true)
        myRecyclerView.layoutManager = LinearLayoutManager(this)
        loadFirebaseData()


        val uid = FirebaseAuth.getInstance().uid

        if(uid == null){
            val intentReg = Intent(this,RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentReg)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intentReg = Intent(this,RegistrationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentReg)
            }
            R.id.account_option->{
                val intentOptions = Intent(this,AccountActivity::class.java)
                startActivity(intentOptions)
            }
            R.id.about_option->{
                val intentAbout = Intent(this,AboutActivity::class.java)
                startActivity(intentAbout)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadFirebaseData(){
        val options= FirebaseRecyclerOptions.
        Builder<City>().setQuery(myDatabase,City::class.java).build()

        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<City,CityViewHolder>(options){
            override fun onBindViewHolder(holder: CityViewHolder, position: Int, model: City) {
                holder.myView.city_textView.text = model.name
                holder.myView.desc_textView.text= model.region
                Picasso.get().load(model.urlPhoto).into(cityPic_View)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
                TODO("Do nothing")
            }

        }

        myRecyclerView.adapter= firebaseRecyclerAdapter
    }

    class CityViewHolder(var myView: View): RecyclerView.ViewHolder(myView){

    }
}