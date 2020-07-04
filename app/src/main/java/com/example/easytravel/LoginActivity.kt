package com.example.easytravel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.mailLog_textView
import kotlinx.android.synthetic.main.activity_registration.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Logbutton.setOnClickListener {
            performLogin()
        }

        regLink_textView.setOnClickListener{
            Log.d("LoginActivity", "Registration Intent")
            var intentReg= Intent(this,RegistrationActivity::class.java)
            startActivity(intentReg)
        }

    }

    private fun performLogin(){
        var email = mailLog_textView.text.toString()
        var password = pass_textView.text.toString()
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_LONG).show()
            return
        }
        Log.d(LoginActivity::class.java.name, "Email: $email")
        Log.d(LoginActivity::class.java.name, "Email: $password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener
                Log.d(LoginActivity::class.java.name, "Success")
                val intentHome= Intent(this,HomeActivity::class.java)
                Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
                  //Do not turn back after registration success
                startActivity(intentHome)
            }
            .addOnFailureListener{
                Toast.makeText(this,"Error on Login: ${it.message}", Toast.LENGTH_LONG).show()
                Log.d(LoginActivity::class.java.name,"Error in login:${it.message}");
            }
    }

}