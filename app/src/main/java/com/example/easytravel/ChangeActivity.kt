package com.example.easytravel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.account_layout.*
import kotlinx.android.synthetic.main.account_layout.changeImage_Button
import kotlinx.android.synthetic.main.account_layout.confirmPassword_EditText
import kotlinx.android.synthetic.main.account_layout.mail_EditText
import kotlinx.android.synthetic.main.account_layout.newPassword_EditText
import kotlinx.android.synthetic.main.account_layout.oldpassword_EditText
import kotlinx.android.synthetic.main.account_layout.save_button
import kotlinx.android.synthetic.main.account_layout.username_EditText
import java.util.*


class ChangeActivity : AppCompatActivity() {
    //field we will fetch form FireBase DB
    lateinit var auth: FirebaseAuth
    var selectedPhotoUri : Uri?= null
    var photo : String= ""
    var username : String=""
    var mail: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_layout)
        supportActionBar?.title="Account"

        val userId = FirebaseAuth.getInstance().uid

        auth= FirebaseAuth.getInstance()
        if (userId != null) {
            fetchUser(userId)
        }

        changeImage_Button.setBackgroundResource(R.drawable.pencil)
        changeImage_Button.setOnClickListener{
            Log.d(ChangeActivity::class.java.name,"Try to show photo")
            var intent= Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }

        save_button.setOnClickListener{
            performChanges()
        }
    }

    /**Fetch the user from DB and show attribute
     * userId: identifier of the client
     *
     */
    private fun fetchUser(userId: String){
        val ref = FirebaseDatabase.
        getInstance().getReference("/users").orderByChild("uid").equalTo(userId)
        Log.d("ChangeActivity", "url:$ref")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("AccountActivity","getUser:${throw error.toException()}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(ChangeActivity::class.java.name, snapshot.toString())
                snapshot.children.forEach{
                    var user = it.getValue(User::class.java)
                    if(user != null){
                        photo= user.profileImageUrl
                        username=user.username
                        mail= user.email
                    }
                }
                Log.d(ChangeActivity::class.java.name, photo)
                Picasso.get().load(photo).into(accountImage_Viewer)
                username_EditText.setText(username)
                mail_EditText.setText(mail)
            }
        })

    }

    private fun performChanges(){
        //Update password
        var user=auth.currentUser
        if(oldpassword_EditText.text.isNotEmpty() &&
            newPassword_EditText.text.isNotEmpty() &&
            confirmPassword_EditText.text.isNotEmpty()){
            if(newPassword_EditText.text.toString() == confirmPassword_EditText.text.toString()){
                if(user != null && user.email != null) {
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!,oldpassword_EditText.text.toString())

                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener{
                            if(it.isSuccessful){
                                user?.updatePassword(newPassword_EditText.text.toString())
                                    ?.addOnCompleteListener{task ->
                                        if(task.isSuccessful){
                                            Toast.makeText(this, "Password changed", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                Toast.makeText(this, "Re-authentication success", Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(this, "Re-authentication failed ", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }else{
                Toast.makeText(this, "Password Mismatch", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Please enter all fields!!", Toast.LENGTH_LONG).show()
        }

        user=auth.currentUser
        //update email
        if(mail_EditText.text.toString() != null){
            val credential = EmailAuthProvider
                .getCredential(mail_EditText.text.toString(),newPassword_EditText.text.toString())

            user?.reauthenticate(credential)
                ?.addOnCompleteListener {
                if(it.isSuccessful){
                    user?.updateEmail(mail_EditText.text.toString())
                        ?.addOnCompleteListener{ task ->
                            if(task.isSuccessful) {
                                Toast.makeText(this, "Mail changed", Toast.LENGTH_LONG).show()
                            }
                        }
                }else{
                    Toast.makeText(this, "Mail not changed", Toast.LENGTH_LONG).show()
                }
            }
        }

        val userId = FirebaseAuth.getInstance().uid
        //Update username and email in DB
        if(username_EditText.text.toString() == null){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_LONG).show()
            return
        }

        val refUpdating = FirebaseDatabase.getInstance().getReference("/users/$userId")
        refUpdating.child("email").setValue(user?.email)
        refUpdating.child("username").setValue(username_EditText.text.toString())

        val intentHome= Intent(this,HomeActivity::class.java)
        //Do not turn back after registration success
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentHome)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d(ChangeActivity::class.java.name, "Photo selected")
            selectedPhotoUri = data.data //Location dove l'immagine é memorizzata
            val bitmapPhoto= MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            accountImage_Viewer.setImageBitmap(bitmapPhoto)
            changeImage_Button.alpha=0f
            //val bitmapPhotoDrawable = BitmapDrawable(bitmapPhoto)
            //ImageButton.setBackgroundDrawable(bitmapPhotoDrawable)
        }
    }

    fun uploadNewImageToFireBase(){
        if (selectedPhotoUri == null) return

        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(photo)
        //Delete old photo and upload the new
        ref.delete().
        addOnSuccessListener {
            Log.d(ChangeActivity::class.java.name, "File deleted")
        }.addOnFailureListener {
                Log.d(ChangeActivity::class.java.name, "Error in file elimination")
            }

        val filename = UUID.randomUUID().toString()

        val uploadRef = FirebaseStorage.getInstance().getReference("/images/$filename")
        uploadRef.putFile(selectedPhotoUri!!)         //At this point in code selectedPhotoUri is never null
            .addOnSuccessListener {
                Log.d(ChangeActivity::class.java.name, "Image stored")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d(RegistrationActivity::class.java.name, it.toString())
                }
            }.addOnFailureListener{
                Log.d(ChangeActivity::class.java.name, "Image not stored")
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
            }
    }
}