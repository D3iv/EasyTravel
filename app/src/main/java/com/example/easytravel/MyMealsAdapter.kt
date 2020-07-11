package com.example.easytravel

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.attribute_layout.view.*

class MyMealsAdapter(val typicalMeal: TypicalMeal): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.attribute_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.mealName_textView.text= typicalMeal.name
        Picasso.get().load(typicalMeal.photoUrl).into(viewHolder.itemView.mealPic_view)
    }
}