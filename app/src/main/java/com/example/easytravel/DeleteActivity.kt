package com.example.easytravel

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.SettingsSlicesContract
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_city_details.*
import kotlinx.android.synthetic.main.activity_delete.*
import kotlinx.android.synthetic.main.activity_delete.view.*

class DeleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dialog = AlertDialog.Builder(this)
        val dialogView= layoutInflater.inflate(R.layout.activity_delete,null)
        dialog.setView(dialogView)
        dialog.setCancelable(false)
        dialogView.btn_yes.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()
                ?.addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        val uid = FirebaseAuth.getInstance().uid

                        val ref = FirebaseDatabase
                            .getInstance().getReference("/user")
                            .child("$uid")

                        ref.addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.ref.removeValue()
                                    .addOnCompleteListener {
                                       Toast.makeText(this@DeleteActivity,"Profile deleted",Toast.LENGTH_LONG).show()
                                    }
                            }
                        })
                        Log.d(DeleteActivity::class.java.name,"Account deleted")
                        val intent = Intent(this,RegistrationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
        }
        dialogView.btn_no.setOnClickListener {
            val intent = Intent(this,AccountActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        dialog.show()
    }
}