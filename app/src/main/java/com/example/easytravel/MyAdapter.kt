package com.example.easytravel

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.list_layout.view.*

class MyAdapter(private val city:City): Item<ViewHolder>()  {
    override fun getLayout(): Int{
        //return layout of the list of cities
        return R.layout.list_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
       //Apply teh fields of all cities in textViews
        viewHolder.itemView.city_textView.text = city.name
        viewHolder.itemView.desc_textView.text = city.region
        Picasso.get().load(city.urlPhoto).into(viewHolder.itemView.cityPic_view)
    }

}
