package com.example.easytravel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.view.Change
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.account_layout.*
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
            var intent= Intent(Intent.ACTION_OPEN_DOCUMENT)
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
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("AccountActivity","getUser:${throw error.toException()}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(ChangeActivity::class.java.name, "Utente trovato: $snapshot")
                snapshot.children.forEach{
                    var user = it.getValue(User::class.java)
                    if(user != null){
                        photo= user.profileImageUrl
                        username=user.username
                        mail= user.email
                    }
                }
                Log.d(ChangeActivity::class.java.name,"Download URL: $photo")
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
                                            //update email
                                            if(mail_EditText.text.toString() != null) {
                                                user?.reauthenticate(credential)
                                                    ?.addOnCompleteListener {
                                                        Log.d(ChangeActivity::class.java.name,"Updating password: "+it.toString())
                                                        if (it.isSuccessful) {
                                                            user?.updateEmail(mail_EditText.text.toString())
                                                                ?.addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        uploadNewImageToFireBase()
                                                                        Toast.makeText(
                                                                            this,
                                                                            "Mail changed",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    }
                                                                }
                                                        }else{
                                                            Toast.makeText(
                                                                this,
                                                                "Mail not changed",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            uploadNewImageToFireBase()
                                                        }
                                                    }
                                            }
                                            Toast.makeText(this, "Password changed", Toast.LENGTH_LONG).show()
                                        }else{
                                            Toast.makeText(this, "Password not changed", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            }
                        }
                }
            }else{
                Toast.makeText(this, "Password Mismatch", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Please enter all fields!!", Toast.LENGTH_LONG).show()
        }

        val userId = user?.uid
        //Update username and email in DB
        if(username_EditText.text.toString() == null){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_LONG).show()
            return
        }

        val refUpdating = FirebaseDatabase.getInstance().getReference("/users/$userId")
        refUpdating.child("email").setValue(mail_EditText.text.toString())
        refUpdating.child("username").setValue(username_EditText.text.toString())

        val intentHome= Intent(this,HomeActivity::class.java)
        //Do not turn back after registration success
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentHome)
        finish()

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
        uploadRef.putFile(selectedPhotoUri!!)                    //At this point in code selectedPhotoUri is never null
            .addOnSuccessListener {
                Log.d(ChangeActivity::class.java.name, "Image stored")
                Toast.makeText(this@ChangeActivity,"File stored",Toast.LENGTH_LONG).show()
                uploadRef.downloadUrl.addOnSuccessListener {
                    Log.d(RegistrationActivity::class.java.name, "Success:$it")
                    updateRealTimeDB(it.toString())
                }

            }.addOnFailureListener{
                Log.d(ChangeActivity::class.java.name, "Image not stored")
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
            }
    }

    private fun updateRealTimeDB(toString: String) {
        val userId= FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
        ref.child("profileImageUrl").setValue(toString)
        Log.d(ChangeActivity::class.java.name,"Image Changed")
        val intentHome= Intent(this,HomeActivity::class.java)
        //Do not turn back after registration success
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentHome)
        finish()
    }


}