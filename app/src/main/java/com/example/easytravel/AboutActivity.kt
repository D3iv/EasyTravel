package com.example.easytravel

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_account.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.title= "About"

        val dialog = AlertDialog.Builder(this)
        val dialogView= layoutInflater.inflate(R.layout.activity_about,null)
        val psw= dialogView.findViewById<TextView>(R.id.about_TextView)
        dialog.setView(dialogView)
        dialog.setOnCancelListener{
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }
        val customDialog= dialog.create()
        dialog.show()
    }
}