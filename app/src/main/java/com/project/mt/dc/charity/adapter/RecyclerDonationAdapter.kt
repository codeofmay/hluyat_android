package com.project.mt.dc.charity.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.MainDonationActivity
import com.project.mt.dc.R
import com.project.mt.dc.charity.activity.DoneFormActivity
import com.project.mt.dc.charity.activity.RequestFormActivity
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import java.util.*


/**
 * Created by mt on 7/29/17.
 */
class RecyclerDonationAdapter(requestList: ArrayList<RequestModel>, activity: Activity, caller: String) : RecyclerView.Adapter<RecyclerDonationAdapter.RequestListViewHolder>() {
    var requestList = ArrayList<RequestModel>()
    var activity: Activity? = null
    var caller: String? = null
    var fontUtil = FontUtil(activity)
    val methodUtil = MethodUtil()
    val charityReference = FirebaseDatabase.getInstance().getReference("charity")
    var fontFlag: Boolean? = null

    init {
        this.requestList = requestList
        this.activity = activity
        this.caller = caller
        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()

    }

    override fun getItemCount(): Int {

        return requestList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RequestListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_charitydonation, parent, false)
        var viewholder = RequestListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: RequestListViewHolder?, position: Int) {
        val requestModel = requestList!![position]
        val formatDate =requestModel.request_date!!
        if (holder != null) {
            if (caller == "donor") {
                charityReference.child(requestModel.charity_id)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(charitySnapshot: DataSnapshot?) {
                                if (charitySnapshot != null) {
                                    val charityModel = charitySnapshot.getValue(CharityModel::class.java)
                                    if (fontFlag!!) {
                                        holder.charityName.text = charityModel.charity_name
                                    }
                                    else{
                                        holder.charityName.text = Rabbit.uni2zg(charityModel.charity_name)
                                    }

                                    Glide.with(activity!!.applicationContext)
                                            .load(charityModel.charity_image)
                                            .placeholder(R.drawable.ic_person_black_24dp)
                                            .bitmapTransform(CropCircleTransformation(activity))
                                            .into(holder.charityImage)
                                }
                            }
                        })
            }

            Glide.with(activity)
                    .load(requestModel.request_image)
                    .into(holder.img_requestImage)


            if (fontFlag!!) {
                holder.lab_donationPlace.text = requestModel.request_place
                holder.lab_donationDate.text = formatDate
                holder.lab_donationDescription.text = requestModel.request_description

            } else {
                holder.lab_donationPlace.text = Rabbit.uni2zg(requestModel.request_place)
                holder.lab_donationDate.text = Rabbit.uni2zg(formatDate)
                holder.lab_donationDescription.text = Rabbit.uni2zg(requestModel.request_description)
            }

        }


    }


    //view holder model
    inner class RequestListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var lab_donationPlace: TextView
        var lab_donationDate: TextView
        var img_requestImage: ImageView
        var lab_show: TextView
        var img_popup: ImageView
        var charityName: TextView
        var charityImage: ImageView
        var lab_donationDescription: TextView
        var linear_charityInfo: LinearLayout

        init {
            lab_donationPlace = itemView.findViewById(R.id.lab_requestplace) as TextView
            lab_donationDate = itemView.findViewById(R.id.lab_requestdate) as TextView
            lab_show = itemView.findViewById(R.id.lab_show) as TextView
            lab_donationDescription = itemView.findViewById(R.id.lab_requestdescription) as TextView
            img_requestImage = itemView.findViewById(R.id.img_requestimage) as ImageView

            charityName = itemView.findViewById(R.id.lab_charityname) as TextView
            charityImage = itemView.findViewById(R.id.img_charityimage) as ImageView
            img_popup = itemView.findViewById(R.id.img_popup) as ImageView
            linear_charityInfo = itemView.findViewById(R.id.linear_charityInfo) as LinearLayout

            charityName.typeface = fontUtil.regular_font
            lab_donationPlace.typeface = fontUtil.title_font
            lab_donationDescription.typeface = fontUtil.regular_font
            lab_donationDate.typeface = fontUtil.light_font


            if (caller == "charity") {
                linear_charityInfo.visibility = View.GONE
            }

            if (caller == "donor") {
                img_popup.visibility = View.GONE
            }

            itemView.setOnClickListener({
                showMainDonation()
            })

            img_popup.setOnClickListener({
                showPopupMenu()
            })


        }

        fun showMainDonation() {
            val requestModel = requestList[position]
            val i = Intent(activity, MainDonationActivity::class.java)
            i.putExtra("donationmodel", requestModel)
            i.putExtra("caller", caller)
            activity!!.startActivity(i)
        }

        fun showPopupMenu() {

            val popup = PopupMenu(activity, img_popup)
            popup.menuInflater.inflate(R.menu.popup_menu_charitydonation, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.popup_edit) {
                    val requestModel = requestList[position]
                    val i = Intent(activity, RequestFormActivity::class.java)
                    i.putExtra("caller", "edit")
                    i.putExtra("requestmodel", requestModel)
                    activity!!.startActivity(i)

                }
                if (item.itemId == R.id.popup_finished) {
                    val requestModel = requestList[position]
                    val i = Intent(activity, DoneFormActivity::class.java)
                    i.putExtra("donemodel", requestModel)
                    activity!!.startActivity(i)
                }
                true
            }
            popup.show()
        }

    }
}
