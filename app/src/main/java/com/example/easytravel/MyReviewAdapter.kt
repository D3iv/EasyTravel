package com.example.easytravel

import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_account.view.*
import kotlinx.android.synthetic.main.activity_account.view.username_textView
import kotlinx.android.synthetic.main.attribute_layout.view.*
import kotlinx.android.synthetic.main.user_list_layout.view.*

class MyReviewAdapter(val review:Review,val photoUrl: String, val name:String): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_list_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.comment_textView.text= "'"+review.comment+"'"
        Log.d(MyReviewAdapter::class.java.name,"${review.uid}")
        val ref = FirebaseDatabase.getInstance().getReference("/users/${review.uid}")
        viewHolder.itemView.username_textView.text= name
        //Picasso.get().load(photoUrl).into(viewHolder.itemView.userPic_view)
    }
}