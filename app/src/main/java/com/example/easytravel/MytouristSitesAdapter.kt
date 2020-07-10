package com.example.easytravel

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_city_details.view.*
import kotlinx.android.synthetic.main.tourist_sites_layout.view.*

class MytouristSitesAdapter(val touristSites: TouristSites): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.activity_sites_details
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.siteName_textView.text = touristSites.name
        Picasso.get().load(touristSites.photoUrl).into(viewHolder.itemView.sitePic_view)
    }
}