package com.project.mt.dc.charity.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.MainDonationActivity
import com.project.mt.dc.R
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


class RecyclerNotificationAdapter(notiList: ArrayList<NotificationModel>, activity: Activity) : RecyclerView.Adapter<RecyclerNotificationAdapter.NotiIdListViewHolder>() {

    var notiList = ArrayList<NotificationModel>()
    var activity: Activity? = null
    var firebaseDB = FirebaseDatabase.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser!!.uid
    var fontUtil:FontUtil
    var notiText1 = activity.getString(R.string.charity_noti1)
    var notiText2 = activity.getString(R.string.charity_noti2)
    var fontFlag: Boolean


    init {
        this.notiList = notiList
        this.activity = activity
        fontUtil= FontUtil(activity)

        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()


        if (!fontFlag) {
            notiText1 = Rabbit.uni2zg(notiText1)
            notiText2 = Rabbit.uni2zg(notiText2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerNotificationAdapter.NotiIdListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_charitynoti, parent, false)
        var viewholder: RecyclerNotificationAdapter.NotiIdListViewHolder = NotiIdListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: RecyclerNotificationAdapter.NotiIdListViewHolder?, position: Int) {
        val detailNotiModel = notiList!![position]
        val notiDuration = MethodUtil().getTimeDifferent(detailNotiModel.noti_duration!!)
        var requestName: String? = null

        if(fontFlag) {
            (holder)!!.itemAmount.text = detailNotiModel.item_amount
            (holder)!!.itemCategory.text = detailNotiModel.item_category
        }
        else{
            (holder)!!.itemAmount.text = Rabbit.uni2zg(detailNotiModel.item_amount)
            (holder)!!.itemCategory.text = Rabbit.uni2zg(detailNotiModel.item_category)
        }
        (holder)!!.notiDuration.text = notiDuration

        firebaseDB.getReference("request_post").child(detailNotiModel.to)
                .child(detailNotiModel.request_id).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(requestSnapshot: DataSnapshot?) {
                if (requestSnapshot != null) {
                    requestName = requestSnapshot.child("request_place").value.toString()
                    if (fontFlag) {
                        // holder.requestName.text = requestName + " " + notiText2
                    } else {
                        requestName = Rabbit.uni2zg(requestName)
                        //holder.requestName.text = Rabbit.uni2zg(requestName) + " " + notiText2
                    }

                }
            }

        })

        firebaseDB.getReference("donor").child(detailNotiModel.from).
                addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(donorSnapshot: DataSnapshot?) {
                        if (donorSnapshot != null) {
                            val donorModel = donorSnapshot!!.getValue(DonorInfoModel::class.java)
                            var donorName = donorModel.donor_name
                            if (fontFlag) {
                                //(holder).donorName.text = donorModel.donor_name+" "+ notiText1+" "+ requestName+" "+notiText2
                            } else {
                                donorName = Rabbit.uni2zg(donorName)
                                //(holder).donorName.text = Rabbit.uni2zg(donorModel.donor_name)
                            }

                            holder.donorName.text = Html.fromHtml("<b>$donorName</b> $notiText1 $requestName $notiText2")

                            Glide.with(activity)
                                    .load(donorModel.donor_image)
                                    .placeholder(R.drawable.ic_person_black_24dp)
                                    .bitmapTransform(CropCircleTransformation(activity))
                                    .into((holder).donorImage)
                        }

                    }

                })
    }
    override fun getItemCount(): Int {
        return notiList.size
    }


    //view holder model
    inner class NotiIdListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemAmount: TextView
        var itemCategory: TextView
        var donorName: TextView
        var notiDuration: TextView
        var donorImage: ImageView

        init {
            itemAmount = itemView.findViewById(R.id.lab_itemamount) as TextView
            itemCategory = itemView.findViewById(R.id.lab_itemcategory) as TextView
            notiDuration = itemView.findViewById(R.id.lab_notiduration) as TextView
            donorName = itemView.findViewById(R.id.lab_donorname) as TextView

            donorImage = itemView.findViewById(R.id.img_donorimage) as ImageView
            itemView.setOnClickListener({
                showMainDonation()
            })

            itemAmount.typeface = fontUtil.title_font
            itemCategory.typeface = fontUtil.title_font
            donorName.typeface = fontUtil.regular_font
            notiDuration.typeface = fontUtil.light_font

        }

        fun showMainDonation() {
            val notiModel = notiList[position]
            val i = Intent(activity, MainDonationActivity::class.java)
            i.putExtra("caller", "charity")
            firebaseDB.getReference("request_post").child(notiModel.to).child(notiModel.request_id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(requestSnapshot: DataSnapshot?) {
                            val requestModel = requestSnapshot!!.getValue(RequestModel::class.java)
                            i.putExtra("donationmodel", requestModel)
                            activity!!.startActivity(i)
                        }

                    })
        }


    }
}