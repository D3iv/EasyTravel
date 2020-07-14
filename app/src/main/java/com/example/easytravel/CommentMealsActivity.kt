package com.example.easytravel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cityName=intent.getStringExtra("cityName")
        name = intent.getStringExtra("mealName")

        val dialog = AlertDialog.Builder(this)
        val dialogView= layoutInflater.inflate(R.layout.activity_comment,null)
        dialog.setView(dialogView)
        dialog.setCancelable(false)

        val mAlertDialog = dialog.show()
        dialogView.publish_button.setOnClickListener {
            mAlertDialog.dismiss()

            val comment=dialogView.comment_EditText.text.toString()

            val refReviews = FirebaseDatabase.getInstance()
                .getReference("/city/$cityName/Typical Meals/$name")

            val uid= FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users").orderByChild("uid").equalTo(uid)
            var photoUrl: String? = null
            var username: String? = null
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                   snapshot.children.forEach{
                       val user= it.getValue(User::class.java)
                       photoUrl = user?.profileImageUrl
                       username = user?.username
                   }
                }

            })

            val review = username?.let { it1 -> photoUrl?.let { it2 ->
                Review(comment,name, it1,
                    it2
                )
            } }
            refReviews.setValue(review)
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
        }
    }
}