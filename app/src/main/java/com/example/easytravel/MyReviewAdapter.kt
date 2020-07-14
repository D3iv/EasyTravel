package com.example.easytravel

import android.view.View
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_account.view.*
import kotlinx.android.synthetic.main.activity_account.view.username_textView
import kotlinx.android.synthetic.main.attribute_layout.view.*
import kotlinx.android.synthetic.main.user_list_layout.view.*

class MyReviewAdapter(val review:Review): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_list_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textView.text= review.username
        viewHolder.itemView.comment_textView.text= "'"+review.comment+"'"
        Picasso.get().load(review.photoUrl).into(viewHolder.itemView.userPic_view)
    }
}