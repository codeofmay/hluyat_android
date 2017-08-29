package com.project.mt.dc.donor.adapter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.MainDonationActivity
import com.project.mt.dc.R
import com.project.mt.dc.R.id.*
import com.project.mt.dc.charity.activity.CharityProfileActivity
import com.project.mt.dc.charity.adapter.RecyclerDonorImagesAdapter
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseService
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


class RecyclerDonatedListAdapter(donatedList: ArrayList<RequestModel>, activity: AppCompatActivity) : RecyclerView.Adapter<RecyclerDonatedListAdapter.ProfileListViewHolder>() {
    var donatedList = ArrayList<RequestModel>()
    var activity: AppCompatActivity? = null
    var firebaseDB = FirebaseDatabase.getInstance()
    var fontFlag: Boolean
    var fontUtil: FontUtil

    init {
        this.donatedList = donatedList
        this.activity = activity
        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()
        fontUtil = FontUtil(activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProfileListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_donordonated, parent, false)
        var viewholder: ProfileListViewHolder = ProfileListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: RecyclerDonatedListAdapter.ProfileListViewHolder, position: Int) {
        val donatedModel = donatedList!![position]
        val donorIdList = ArrayList<String>()

        //get donor Image list
        firebaseDB.getReference("charity")
                .child(donatedModel.charity_id)
                .child("donor_list")
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
                            (holder).recycler_donorImages.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                })


        //get charity profile
        firebaseDB.getReference("charity").child(donatedModel.charity_id).
                addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(charitySnapshot: DataSnapshot?) {
                        if (charitySnapshot != null) {
                            val charityModel=charitySnapshot.getValue(CharityModel::class.java)
                            if(fontFlag) {
                                (holder).charity_name.text = charityModel.charity_name
                            }
                            else{
                                (holder).charity_name.text = Rabbit.uni2zg(charityModel.charity_name)
                            }
                            Glide.with(activity)
                                    .load(charityModel.charity_image)
                                    .placeholder(R.drawable.ic_person_black_24dp)
                                    .bitmapTransform(CropCircleTransformation(activity))
                                    .into((holder).img_charityImage)
                        }
                    }

                })

        //get done post
        firebaseDB.getReference("request_post")
                .child(donatedModel.charity_id)
                .child(donatedModel.request_id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(requestSnapshot: DataSnapshot?) {
                        val doneModel = requestSnapshot!!.getValue(RequestModel::class.java)
                        if(fontFlag) {
                            (holder)!!.done_place.text = doneModel.request_place
                            (holder).done_date.text = doneModel.request_date
                        }
                        else{
                            (holder)!!.done_place.text = Rabbit.uni2zg(doneModel.request_place)
                            (holder).done_date.text = Rabbit.uni2zg(doneModel.request_date)
                        }

                        donatedModel.request_place=doneModel.request_place
                        donatedModel.request_image=doneModel.request_image
                        donatedModel.request_date=doneModel.request_date
                        donatedModel.request_description=doneModel.request_description
                        donatedModel.request_location=donatedModel.request_location

                        Glide.with(activity)
                                .load(doneModel.request_image)
                                .into((holder).done_image)

                    }

                })

        //get donated item
        val map = donatedModel.item_list
        var itemString: String? = null
        val itemList = ArrayList<ItemInfoModel>(map!!.values)

        for (itemModel in itemList) {

            if (itemString == null) {
                itemString = itemModel.item_category + " : " + itemModel.item_amount + "\n"
            } else {
                itemString = itemString + itemModel.item_category + " : " + itemModel.item_amount + "\n"
            }
        }
        if(fontFlag) {
            (holder).lab_items.text = itemString
        }
        else{
            (holder).lab_items.text = Rabbit.uni2zg(itemString)
        }

    }

    override fun getItemCount(): Int {
        return donatedList.size
    }

    inner class ProfileListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var done_place: TextView
        var done_image: ImageView
        var done_date: TextView
        var charity_name: TextView
        var lab_items: TextView
        var img_charityImage: ImageView
        var card_donated: LinearLayout
        var recycler_donorImages: RecyclerView
        var linear_profileMore: LinearLayout
        var lab_via:TextView
        var lab_on:TextView
        var lab_donatedto:TextView

        init {
            done_place = itemView.findViewById(lab_doneplace) as TextView
            done_image = itemView.findViewById(img_doneimage) as ImageView
            done_date = itemView.findViewById(R.id.lab_donedate) as TextView

            lab_via = itemView.findViewById(R.id.lab_via) as TextView
            lab_on= itemView.findViewById(R.id.lab_on) as TextView
            lab_donatedto=itemView.findViewById(R.id.lab_donatedto)as TextView

            charity_name = itemView.findViewById(lab_charityname) as TextView
            lab_items = itemView.findViewById(R.id.lab_items) as TextView
            img_charityImage = itemView.findViewById(R.id.img_charityimage) as ImageView
            card_donated = itemView.findViewById(R.id.card_donated) as LinearLayout
            recycler_donorImages = itemView.findViewById(R.id.recycler_donorsimage) as RecyclerView
            linear_profileMore = itemView.findViewById(R.id.linear_profilemore) as LinearLayout

            done_place.typeface = fontUtil.title_font
            done_date.typeface = fontUtil.light_font
            charity_name.typeface=fontUtil.light_font
            lab_via.typeface=fontUtil.light_font
            lab_on.typeface=fontUtil.light_font
            lab_items.typeface=fontUtil.regular_font
            lab_donatedto.typeface=fontUtil.title_font

            val layoutManager: LinearLayoutManager = LinearLayoutManager(FirebaseService().getContext(), LinearLayoutManager.HORIZONTAL, false)
            recycler_donorImages!!.layoutManager = layoutManager

            img_charityImage.setOnClickListener({
                val charityId = donatedList[position!!].charity_id
                val i = Intent(activity, CharityProfileActivity::class.java)
                i.putExtra("caller", "visitor")
                i.putExtra("id", charityId)
                activity!!.startActivity(i)
            })

            linear_profileMore.setOnClickListener({
                val donatedModel = donatedList[position]
                val i = Intent(activity, MainDonationActivity::class.java)
                i.putExtra("caller", "donated")
                i.putExtra("donationmodel", donatedModel)
                activity!!.startActivity(i)
            })

        }

    }
}