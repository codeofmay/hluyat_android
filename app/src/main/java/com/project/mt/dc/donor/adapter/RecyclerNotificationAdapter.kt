package com.project.mt.dc.donor.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Html
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
import com.project.mt.dc.R
import com.project.mt.dc.donor.activity.DonorProfileActivity
import com.project.mt.dc.donor.activity.NotiDetailActivity
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit

class RecyclerNotificationAdapter(notiList: ArrayList<NotificationModel>, activity: Activity) : RecyclerView.Adapter<RecyclerNotificationAdapter.notiIdListViewHolder>() {

    var notiList = ArrayList<NotificationModel>()
    var activity: android.app.Activity? = null
    val firebaseAuth = FirebaseAuth.getInstance()
    val fontUtil = FontUtil(activity)
    var fontFlag:Boolean?= null
    val currentUser = firebaseAuth.currentUser!!.uid
    val firebaseDB = FirebaseDatabase.getInstance()
    val firebaseWriteService=FirebaseWriteService()
    var donor_noti_request:String?= null
    var donor_noti_donated:String?= null
    var donor_noti_text:String?= null

    init {
        this.notiList = notiList
        this.activity = activity
        MDetect.init(activity)
        fontFlag=MDetect.isUnicode()

        if(fontFlag!!){
            donor_noti_request=activity.getString(R.string.donor_noti_request)
            donor_noti_donated=activity.getString(R.string.donor_noti_donated)
            donor_noti_text=activity.getString(R.string.charity_noti1)
        }
        else{
            donor_noti_request=Rabbit.uni2zg(activity.getString(R.string.donor_noti_request))
            donor_noti_donated=Rabbit.uni2zg(activity.getString(R.string.donor_noti_donated))
            donor_noti_text=Rabbit.uni2zg(activity.getString(R.string.charity_noti1))
        }
    }

    override fun getItemCount(): Int {
        return notiList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerNotificationAdapter.notiIdListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_donornoti, parent, false)
        var viewholder: RecyclerNotificationAdapter.notiIdListViewHolder = notiIdListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: RecyclerNotificationAdapter.notiIdListViewHolder?, position: Int) {

        val detailNotiModel = notiList!![position]

        if(detailNotiModel.noti_type == "request") {

        val item = firebaseDB.getReference("donateitems").
                child(detailNotiModel.item_id)

        item.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(itemSnapshot: DataSnapshot?) {

                if (itemSnapshot != null) {
                    if (itemSnapshot.child("item_status").value == "Accepted"
                            && itemSnapshot.child("accept_notiId").value == detailNotiModel.noti_id) {
                        (holder)!!.img_status.setImageResource(R.drawable.ic_accepted)
                        (holder).background.setBackgroundColor(Color.parseColor("#eeeeee"))
                        detailNotiModel.noti_status = "accepted"

                    } else if (itemSnapshot.child("item_status").value == "Accepted") {
                        (holder)!!.img_status.setImageResource(R.drawable.ic_deny)
                        (holder).background.setBackgroundColor(Color.parseColor("#eeeeee"))
                        detailNotiModel.noti_status = "denied"
                    } else {
                        detailNotiModel.noti_status = "nothing"
                    }
                }

            }

        })

            if(fontFlag!!) {
                (holder)!!.itemAmount.text = detailNotiModel.item_amount
                (holder).itemCategory.text = detailNotiModel.item_category
            }
            else{
                (holder)!!.itemAmount.text = Rabbit.uni2zg(detailNotiModel.item_amount)
                (holder).itemCategory.text = Rabbit.uni2zg(detailNotiModel.item_category)
            }

        }
        else{
            (holder)!!.itemAmount.visibility=View.GONE
            (holder)!!.itemCategory.visibility=View.GONE
            (holder)!!.lab_donornoti.visibility=View.GONE
        }

        val notiDuration = MethodUtil().getTimeDifferent(detailNotiModel.noti_duration!!)
        (holder)!!.notiDuration.text = notiDuration
        if (detailNotiModel.from != null) {
            val charityReference=firebaseDB.getReference("charity").child(detailNotiModel.from)
            charityReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(charitysnapshot: DataSnapshot?) {
                    if (charitysnapshot != null) {
                        val charityModel = charitysnapshot.getValue(CharityModel::class.java)
                        var charityName:String?
                        if(fontFlag!!) {
                            charityName=charityModel.charity_name
                        }
                        else{
                            charityName=Rabbit.uni2zg(charityModel.charity_name)
                        }
                        if (detailNotiModel.noti_type=="request") {
                            holder.charityName.text = Html.fromHtml("<b>$charityName</b>$donor_noti_text $donor_noti_request")
                        }
                        else{
                            var requestReference=firebaseDB.getReference("request_post")
                            requestReference.child(detailNotiModel.from)
                                    .child(detailNotiModel.request_id)
                                    .addListenerForSingleValueEvent(object :ValueEventListener{
                                        override fun onCancelled(p0: DatabaseError?) {

                                        }

                                        override fun onDataChange(requestSnapShot: DataSnapshot?) {
                                            if (requestSnapShot != null) {

                                                //set read unread
                                                if(requestSnapShot.child("read_status").exists()){
                                                    (holder).background.setBackgroundColor(Color.parseColor("#eeeeee"))
                                                }
                                                var requestName:String?
                                                charityModel
                                                if(fontFlag!!){
                                                    requestName=requestSnapShot.child("request_place").value.toString()
                                                }
                                                else{
                                                    requestName=Rabbit.uni2zg(requestSnapShot.child("request_place").value.toString())
                                                }
                                                holder.charityName.text = Html.fromHtml("<b>$charityName</b>$donor_noti_text <b>$requestName</b>$donor_noti_donated")

                                            }
                                        }

                                    })

                        }
                        Glide.with(activity!!.applicationContext).
                                load(charityModel.charity_image)
                                .placeholder(R.drawable.ic_person_black_24dp)
                                .bitmapTransform(CropCircleTransformation(activity!!.applicationContext)).
                                into((holder).charityImage)
                        detailNotiModel.charity_model = charityModel
                    }


                }
                override fun onCancelled(p0: DatabaseError?) {

                }
            })

        }
    }

    //view holder model
    inner class notiIdListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemAmount: TextView
        var itemCategory: TextView
        var charityName: TextView
        var notiDuration: TextView
        var charityImage: ImageView
        var background: LinearLayout
        var img_status: ImageView
        var lab_donornoti: TextView


        init {
            itemAmount = itemView.findViewById(R.id.lab_itemamount) as TextView
            itemCategory = itemView.findViewById(R.id.lab_itemcategory) as TextView
            notiDuration = itemView.findViewById(R.id.lab_notiduration) as TextView
            lab_donornoti=itemView.findViewById(R.id.textView1_donornoti)as TextView

            charityName = itemView.findViewById(R.id.lab_charityname) as TextView

            charityImage = itemView.findViewById(R.id.img_charityimage) as ImageView
            img_status = itemView.findViewById(R.id.img_notistatus) as ImageView
            background = itemView.findViewById(R.id.linear_donor_noti) as LinearLayout

            charityName.typeface = fontUtil.regular_font
            itemCategory.typeface = fontUtil.regular_font
            itemCategory.typeface = fontUtil.regular_font
            notiDuration.typeface = fontUtil.regular_font


            itemView.setOnClickListener({
                showDetail()
            })


        }

        fun showDetail() {
            var itemNotiModel = notiList[position]
            if(itemNotiModel.noti_type =="request") {
                val i = android.content.Intent(activity, NotiDetailActivity::class.java)
                i.putExtra("itemNotiModel", itemNotiModel)
                activity!!.startActivity(i)
            }
            if(itemNotiModel.noti_type=="done"){
                firebaseWriteService.setStatus("request_post", itemNotiModel.from!!,
                        itemNotiModel.request_id!!,"read_status", "yes")

                val i=Intent(activity,DonorProfileActivity::class.java)
                i.putExtra("caller","owner")
                activity!!.startActivity(i)
            }
        }

    }
}
