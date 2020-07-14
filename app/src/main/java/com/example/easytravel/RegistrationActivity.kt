package com.example.easytravel

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    var selectedPhotoUri: Uri?=null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        reg_Button.setOnClickListener {
            performRegister()
        }

        linkLog_textView.setOnClickListener {
            Log.d(RegistrationActivity::class.java.name, "Login Intent")
            var intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        }

        ImageButton.setOnClickListener{
            Log.d(RegistrationActivity::class.java.name,"Try to show photo")
            var intent= Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d(RegistrationActivity::class.java.name, "Photo selected")
            selectedPhotoUri = data.data //Location dove l'immagine é memorizzata
            val bitmapPhoto= MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            imageViewRegister.setImageBitmap(bitmapPhoto)
            ImageButton.alpha=0f
        }
    }

    private fun performRegister(){
        var email = mailLog_textView.text.toString()
        var username = name_textView.text.toString()
        var password = password_textView.text.toString()
        if(email.isEmpty() || password.isEmpty() || username.isEmpty()){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_LONG).show()
            return
        }
        Log.d(RegistrationActivity::class.java.name, "$email")
        Log.d(RegistrationActivity::class.java.name, "$username")
        Log.d(RegistrationActivity::class.java.name, "$password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener
                Log.d(RegistrationActivity::class.java.name, "registration Successfull")
                Toast.makeText(this, "Success",Toast.LENGTH_LONG).show()
                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener{
                Toast.makeText(this, "Error on Registration: ${it.message}",Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)         //At this point in code selectedPhotoUri is never null
            .addOnSuccessListener {
                Log.d(RegistrationActivity::class.java.name, "Image stored")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d(RegistrationActivity::class.java.name, it.toString())
                    saveUserToFirebaseDatabase(it.toString())
                }
            }.addOnFailureListener{
                Log.d(RegistrationActivity::class.java.name, "Image not stored")
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val username = name_textView.text.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val email= mailLog_textView.text.toString()

        val user = User(uid,username,profileImageUrl,email)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(RegistrationActivity::class.java.name, "User saved in DB")
                val intentHome= Intent(this,HomeActivity::class.java)
                //Do not turn back after registration success
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentHome)
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,"Failed to save user on DB", Toast.LENGTH_LONG).show()
            }
    }
}
