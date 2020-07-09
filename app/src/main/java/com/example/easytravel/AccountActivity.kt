package com.example.easytravel

import android.accounts.Account
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account.*
import java.util.*

class AccountActivity : AppCompatActivity() {
    //field we will fetch form FireBase DB
    var photo : String= ""
    var username : String=""
    var mail: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        supportActionBar?.title="Account"

        val userId = FirebaseAuth.getInstance().uid
        if (userId != null) {
            fetchUser(userId)
        }

        deleteAccount_textView.setOnClickListener{
            val intentDelete = Intent(this,DeleteActivity::class.java)
            startActivity(intentDelete)
        }

        changeBio_textView.setOnClickListener{
            val intentChange = Intent(this,ChangeActivity::class.java)
            startActivity(intentChange)
        }
    }

    /**Fetch the user from DB and show attributes
     * userId: identifier of the client
     *
      */
    private fun fetchUser(userId: String){
        val ref = FirebaseDatabase.
            getInstance().getReference("/users").orderByChild("uid").equalTo(userId)
        Log.d("AccountActivity", "url:$ref")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Log.d("AccountActivity","getUser:${throw error.toException()}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(AccountActivity::class.java.name, snapshot.toString())
                    snapshot.children.forEach{
                        val user = it.getValue(User::class.java)
                        if(user != null){
                            photo= user.profileImageUrl
                            username=user.username
                            mail= user.email
                        }
                    }
                    Picasso.get().load(photo).into(accountImage_Viewer)
                    username_textView.text = username
                    mail_textView.text= mail
                }
        })
    }
}