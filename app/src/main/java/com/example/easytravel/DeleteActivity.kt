package com.example.easytravel

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_city_details.*
import kotlinx.android.synthetic.main.activity_delete.*

class DeleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_delete)

        btn_yes.setOnClickListener{
            val user= FirebaseAuth.getInstance().currentUser
            val credential = EmailAuthProvider.getCredential(user?.email!!,password_textView.text.toString())

            user.reauthenticate(credential)
                .addOnCompleteListener() {
                    if(it.isSuccessful){
                        user.delete().addOnCompleteListener {task ->
                            if(task.isSuccessful) {
                                //Remove user from RealTimeDB
                                val userId = FirebaseAuth.getInstance().uid
                                val RTuser = FirebaseDatabase.getInstance().getReference("/users").child("$userId")

                                RTuser.setValue(null).addOnCompleteListener {
                                    if(task.isSuccessful){
                                        Toast.makeText(this,"Record deleted",Toast.LENGTH_LONG).show()
                                        finish()
                                    }else{
                                        Toast.makeText(this,"Record not deleted",Toast.LENGTH_LONG).show()
                                    }
                                }
                                
                                Toast.makeText(this, "Account deleted", Toast.LENGTH_LONG).show()
                                val intentRegistration =
                                    Intent(this, RegistrationActivity::class.java)
                                //Do not turn back after registration success
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intentRegistration)
                            }
                        }
                    }else{
                        Toast.makeText(this,"Account not deleted",Toast.LENGTH_LONG).show()
                        val intentAccount= Intent(this,AccountActivity::class.java)
                        startActivity(intentAccount)
                    }
                }
        }

        btn_no.setOnClickListener{
            val intentAccount= Intent(this,AccountActivity::class.java)
            startActivity(intentAccount)
        }


    }
}