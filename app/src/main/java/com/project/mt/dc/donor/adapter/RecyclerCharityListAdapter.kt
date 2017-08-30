package com.project.mt.dc.donor.adapter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.R
import com.project.mt.dc.charity.activity.CharityProfileActivity
import com.project.mt.dc.donor.activity.CharityUpcomingDonationActivity
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit

/**
 * Created by mt on 8/1/17.
 */
class RecyclerCharityListAdapter(charityList: ArrayList<CharityModel>, activity: AppCompatActivity) : RecyclerView.Adapter<RecyclerCharityListAdapter.charityListListViewHolder>() {

    var charityList = ArrayList<CharityModel>()
    var activity: AppCompatActivity? = null
    val requestReference = FirebaseDatabase.getInstance().getReference("request_post")
    var fontUtil: FontUtil? = null
    var fontFlag: Boolean? = null

    init {
        this.charityList = charityList
        this.activity = activity
        fontUtil = FontUtil(activity)
        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()

    }

    override fun getItemCount(): Int {
        return charityList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): charityListListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_charitylist, parent, false)
        var viewholder = charityListListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: charityListListViewHolder?, position: Int) {

        val charityModel = charityList!![position]


        if (fontFlag!!) {
            (holder)!!.charityName.text = charityModel.charity_name
        } else {
            (holder)!!.charityName.text = Rabbit.uni2zg(charityModel.charity_name)
        }
        holder.charityYear.text = "Since " + charityModel.charity_year
        Glide.with(activity).
                load(charityModel.charity_image).
                placeholder(R.drawable.ic_person_black_24dp).
                bitmapTransform(CropCircleTransformation(activity))
                .into((holder).charityImage)

        var count = 0
        requestReference.child(charityModel.charity_id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(requestSnapShot: DataSnapshot?) {
                        if (requestSnapShot != null) {
                            for (rSnapshot: DataSnapshot in requestSnapShot.children) {
                                if (rSnapshot.child("request_status").value.toString() == "requesting") {
                                    count++
                                }
                            }
                            holder.requestCount.text = count.toString()

                        }
                    }

                })


    }


    //view holder model
    inner class charityListListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var charityName: TextView
        var charityYear: TextView
        var charityImage: ImageView
        var requestCount: TextView


        init {

            charityName = itemView.findViewById(R.id.lab_charityname) as TextView
            charityYear = itemView.findViewById(R.id.lab_charityyear) as TextView
            charityImage = itemView.findViewById(R.id.img_charityimage) as ImageView
            requestCount = itemView.findViewById(R.id.lab_requestcount) as TextView

            charityName.typeface = fontUtil!!.title_font
            charityYear.typeface = fontUtil!!.light_font
            requestCount.typeface = fontUtil!!.title_font

            itemView.setOnClickListener({
                val charityModel = charityList[position]
                val i = Intent(activity, CharityUpcomingDonationActivity::class.java)
                i.putExtra("charitymodel", charityModel)
                activity!!.startActivity(i)

            })

            charityName.setOnClickListener({
                val charityModel = charityList[position]
                val i = Intent(activity, CharityProfileActivity::class.java)
                i.putExtra("caller", "visitor")
                i.putExtra("id", charityModel.charity_id)
                activity!!.startActivity(i)
            })

            charityImage.setOnClickListener({
                val charityModel = charityList[position]
                val i = Intent(activity, CharityProfileActivity::class.java)
                i.putExtra("caller", "visitor")
                i.putExtra("id", charityModel.charity_id)
                activity!!.startActivity(i)
            })


        }


    }
}
