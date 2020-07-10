package com.example.easytravel

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.list_layout.*
import kotlinx.android.synthetic.main.list_layout.view.*
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
}