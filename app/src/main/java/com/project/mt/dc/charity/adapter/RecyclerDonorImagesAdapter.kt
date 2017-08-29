package com.project.mt.dc.charity.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.R
import jp.wasabeef.glide.transformations.CropCircleTransformation

/**
 * Created by mt on 7/6/17.
 */
class RecyclerDonorImagesAdapter(donorIdList: ArrayList<String>, activity: Activity) : RecyclerView.Adapter<RecyclerDonorImagesAdapter.ImageViewHolder>() {
    var donorIdList = ArrayList<String>()
    var activity: Activity? = null

    init {
        this.donorIdList = donorIdList
        this.activity = activity

    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_donorimages, parent, false)
        var viewholder: ImageViewHolder = ImageViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        FirebaseDatabase.getInstance().getReference("donor").child(donorIdList!![position]).
                addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(donorSnapshot: DataSnapshot?) {
                        if (donorSnapshot != null) {
                            val imageUrl= donorSnapshot.child("donor_image").value
                            Glide.with(activity!!.applicationContext)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_person_black_24dp)
                                    .bitmapTransform(CropCircleTransformation(activity))
                                    .into((holder).donorImage)
                        }
                    }

                })

    }

    override fun getItemCount(): Int {
        return donorIdList.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var donorImage: ImageView


        init {
            donorImage = itemView.findViewById(R.id.img_donorimage) as ImageView
        }
    }
}