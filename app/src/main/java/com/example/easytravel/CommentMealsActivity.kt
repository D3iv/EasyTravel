package com.example.easytravel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_comment.view.*

class CommentMealsActivity : AppCompatActivity() {
    private lateinit var cityName: String
    private lateinit var name: String
    private var user:User ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cityName=intent.getStringExtra("cityName")
        name = intent.getStringExtra("mName")

        val dialog = AlertDialog.Builder(this)
        val dialogView= layoutInflater.inflate(R.layout.activity_comment,null)
        dialog.setView(dialogView)
        dialog.setCancelable(false)

        val mAlertDialog = dialog.show()
        dialogView.publish_button.setOnClickListener {
            val uid= FirebaseAuth.getInstance().uid
        /*

            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.d(CommentMealsActivity::class.java.name, "Error: $it")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d(CommentMealsActivity::class.java.name, "User Profile: $snapshot")
                            user= snapshot.getValue(User::class.java)
                    }
                })
         */
            //Log.d(CommentMealsActivity::class.java.name, "User Profile: ${user?.username}, ${user?.profileImageUrl}")
            val comment=dialogView.comment_EditText.text.toString()
            val review = Review(comment,uid.toString(),name)

            val refAddReviews = FirebaseDatabase.getInstance().getReference("/review/$uid")
            refAddReviews.setValue(review)
                .addOnSuccessListener {
                    Toast.makeText(this,"Comment added",Toast.LENGTH_LONG).show()
                    val intentComment= Intent(this,HomeActivity::class.java)
                    //Do not turn back after registration success
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    finish()
                    startActivity(intentComment)
                }.addOnFailureListener{
                    Toast.makeText(this,"Failed to save on DB", Toast.LENGTH_LONG).show()
                    finish()
                }
            mAlertDialog.dismiss()
        }
    }
}