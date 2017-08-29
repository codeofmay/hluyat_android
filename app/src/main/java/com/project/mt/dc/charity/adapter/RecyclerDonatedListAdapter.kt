package com.project.mt.dc.charity.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.MainDonationActivity
import com.project.mt.dc.R
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseService
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


class RecyclerDonatedListAdapter(donatedList: ArrayList<RequestModel>,currentUser:String, activity: Activity) : RecyclerView.Adapter<RecyclerDonatedListAdapter.ProfileListViewHolder>() {
    var donatedList = ArrayList<RequestModel>()
    var activity: Activity? = null
    var currentUser = currentUser
    val fontUtil = FontUtil(activity)
    var fontFlag: Boolean? = null

    init {
        this.donatedList = donatedList
        this.activity = activity
        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProfileListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_charity_donated, parent, false)
        var viewholder: ProfileListViewHolder = ProfileListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: RecyclerDonatedListAdapter.ProfileListViewHolder, position: Int) {
        val donatedModel = donatedList!![position]
        val donorIdList = ArrayList<String>()

        val place = donatedModel.request_place
        val description = donatedModel.request_description

        if (fontFlag!!) {
            (holder)!!.done_place.text = place
            (holder)!!.done_description.text = description
        }
        else{
            (holder)!!.done_place.text = Rabbit.uni2zg(place)
            (holder)!!.done_description.text = Rabbit.uni2zg(description)
        }

        (holder).done_date.text=donatedModel.request_date
        Glide.with(activity)
                .load(donatedModel.request_image)
                .into((holder).done_image)

        val charityReference = FirebaseDatabase.getInstance().getReference("charity").child(currentUser)
        charityReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot?) {
                val charityModel = datasnapshot!!.getValue(CharityModel::class.java)

                Glide.with(activity!!.applicationContext)
                        .load(charityModel.charity_image)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .bitmapTransform(CropCircleTransformation(activity))
                        .into((holder).charity_image)

            }

            override fun onCancelled(p0: com.google.firebase.database.DatabaseError?) {

            }
        })

        charityReference.child("donor_list")
                .child(donatedModel.request_id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(donorListSnapshot: DataSnapshot?) {
                        if (donorListSnapshot != null) {
                            donorIdList.clear()
                            for (dSnapshot: DataSnapshot in donorListSnapshot.children) {
                                val donorId = dSnapshot.child("donor_id").value.toString()
                                donorIdList.add(donorId)
                            }
                            val hs = HashSet<String>()
                            hs.addAll(donorIdList)
                            donorIdList.clear()
                            donorIdList.addAll(hs)
                            val adapter = RecyclerDonorImagesAdapter(donorIdList, activity!!)
                            (holder).recycler_donorImage.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                })

    }

    override fun getItemCount(): Int {
        return donatedList.size
    }

    inner class ProfileListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var done_place: TextView
        var done_image: ImageView
        var done_description: TextView
        var linear_profileMore:LinearLayout
        var done_date: TextView
        var charity_image: ImageView
        var recycler_donorImage: RecyclerView
        var lab_text:TextView
        var lab_on:TextView

        init {
            done_place = itemView.findViewById(R.id.lab_doneplace) as TextView
            done_image = itemView.findViewById(R.id.img_doneimage) as ImageView
            charity_image = itemView.findViewById(R.id.img_charityimage) as ImageView
            done_date = itemView.findViewById(R.id.lab_donedate) as TextView
            done_description = itemView.findViewById(R.id.lab_donedescription) as TextView
            lab_text = itemView.findViewById(R.id.lab_text) as TextView
            lab_on= itemView.findViewById(R.id.lab_on) as TextView
            recycler_donorImage = itemView.findViewById(R.id.recycler_donorsimage) as RecyclerView
            linear_profileMore=itemView.findViewById(R.id.linear_profilemore)as LinearLayout

            done_place.typeface = fontUtil.title_font
            done_date.typeface = fontUtil.light_font
            done_description.typeface = fontUtil.regular_font
            lab_text.typeface=fontUtil.title_font
            lab_on.typeface=fontUtil.light_font

            val layoutManager: LinearLayoutManager = LinearLayoutManager(FirebaseService().getContext(), LinearLayoutManager.HORIZONTAL, false)
            recycler_donorImage!!.layoutManager = layoutManager

            linear_profileMore.setOnClickListener({
                val donatedModel=donatedList[position]
                val i=Intent(activity,MainDonationActivity::class.java)
                i.putExtra("caller","donated")
                i.putExtra("donationmodel",donatedModel)
                activity!!.startActivity(i)
            })
        }
    }
}