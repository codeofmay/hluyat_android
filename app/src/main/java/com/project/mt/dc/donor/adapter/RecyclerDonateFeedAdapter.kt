package com.project.mt.dc.donor.adapter

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.MainDonationActivity
import com.project.mt.dc.R
import com.project.mt.dc.donor.activity.DonorProfileActivity
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


/**
 * Created by mt on 6/25/17.
 */
class RecyclerDonateFeedAdapter(itemList: ArrayList<ItemInfoModel>, activity: Activity) : RecyclerView.Adapter<RecyclerDonateFeedAdapter.DonateItemListViewHolder>() {
    var itemList = ArrayList<ItemInfoModel>()
    var activity: Activity? = null
    val fontUtil = FontUtil(activity)
    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    var fontFlag: Boolean? = null
    var status_donating: String? = null
    var status_accepted: String? = null
    val firebaseDB = FirebaseDatabase.getInstance()


    init {
        this.itemList = itemList
        this.activity = activity
        MDetect.init(activity)

        fontFlag = MDetect.isUnicode()
        if (fontFlag!!) {
            status_donating = activity.getString(R.string.item_status_donating)
            status_accepted = activity.getString(R.string.item_status_accepted)
        } else {
            status_donating = Rabbit.uni2zg(activity.getString(R.string.item_status_donating))
            status_accepted = Rabbit.uni2zg(activity.getString(R.string.item_status_accepted))
        }

    }

    override fun getItemCount(): Int {
        return itemList!!.size
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DonateItemListViewHolder {
        var viewholder: DonateItemListViewHolder?
        val v: View
        v = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_donatefeed, parent, false)
        viewholder = DonateItemListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: DonateItemListViewHolder?, position: Int) {
        var holder = holder as DonateItemListViewHolder

        val itemInfoModel = itemList!![position]
        val itemDuration = MethodUtil().getTimeDifferent(itemInfoModel.item_time!!)

        if (fontFlag!!) {
            (holder).itemAmount.text = itemInfoModel.item_amount
            (holder).itemCategory.text = itemInfoModel.item_category
        } else {
            (holder).itemAmount.text = Rabbit.uni2zg(itemInfoModel.item_amount)
            (holder).itemCategory.text = Rabbit.uni2zg(itemInfoModel.item_category)
        }

        (holder).itemDuration.text = itemDuration
        Glide.with(activity)
                .load(itemInfoModel.item_image)
                .asBitmap()
                .into((holder).itemImage)
        (holder).itemStatus.text = itemInfoModel.item_status

        if (itemInfoModel.item_status == "Accepted") {
            holder.itemStatus.text = "ccepted"
            holder.relative_donated.visibility = View.VISIBLE
            holder.relative_pending.visibility = View.GONE
        }

        if (itemInfoModel.item_status == "Donating") {
            holder.itemStatus.text = "onating"
            holder.relative_donated.visibility = View.GONE
            holder.relative_pending.visibility = View.VISIBLE
        }

        val donorReference = FirebaseDatabase.getInstance().getReference("donor").child(itemInfoModel.donor_id)

        donorReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot?) {
                val donorModel: DonorInfoModel = datasnapshot!!.getValue(DonorInfoModel::class.java)

                if (fontFlag!!) {
                    (holder).donorName.text = donorModel.donor_name
                } else {
                    (holder).donorName.text = Rabbit.uni2zg(donorModel.donor_name)
                }


                Glide.with(activity)
                        .load(donorModel.donor_image)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .bitmapTransform(CropCircleTransformation(activity))
                        .into((holder).donorImage)

            }

            override fun onCancelled(p0: com.google.firebase.database.DatabaseError?) {

            }
        })


    }

    //view holder model
    inner class DonateItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemAmount: TextView
        var itemCategory: TextView
        var itemImage: com.github.siyamed.shapeimageview.RoundedImageView
        var itemDuration: TextView
        var donorName: TextView
        var donorImage: ImageView
        var itemStatus: TextView
        var relative_donated: RelativeLayout
        var relative_pending: RelativeLayout

        init {

            itemAmount = itemView.findViewById(R.id.lab_itemamount) as TextView
            itemCategory = itemView.findViewById(R.id.lab_itemcategory) as TextView
            itemDuration = itemView.findViewById(R.id.lab_duration) as TextView
            itemImage = itemView.findViewById(R.id.img_itemimage) as com.github.siyamed.shapeimageview.RoundedImageView
            itemStatus = itemView.findViewById(R.id.lab_itemstatus) as TextView
            donorName = itemView.findViewById(R.id.lab_pdonorname) as TextView
            donorImage = itemView.findViewById(R.id.img_pdonorimage) as ImageView
            relative_donated = itemView.findViewById(R.id.relative_donated) as RelativeLayout
            relative_pending = itemView.findViewById(R.id.relative_pending) as RelativeLayout
            //img_statusIndicator=itemView.findViewById(R.id.img_statusindicator)as ImageView

            itemImage.radius=6
            donorImage.setOnClickListener({

                val donorId = itemList[position!!].donor_id
                val i = Intent(activity, DonorProfileActivity::class.java)
                if (donorId == currentUser) {
                    i.putExtra("caller", "owner")

                } else {
                    i.putExtra("caller", "visitor")
                    i.putExtra("id", donorId)
                }
                activity!!.startActivity(i)
            })

            donorName.setOnClickListener({
                val donorId = itemList[position!!].donor_id
                val i = Intent(activity, DonorProfileActivity::class.java)
                if (donorId == currentUser) {
                    i.putExtra("caller", "owner")

                } else {
                    i.putExtra("caller", "visitor")
                    i.putExtra("id", donorId)
                }
                activity!!.startActivity(i)
            })

            itemView.setOnClickListener {
                val itemModel = itemList[position]
                if (itemModel.item_status == "Donating") {
                    showDonatingStatusDialog()
                }

                if (itemModel.item_status == "Accepted") {
                    showAcceptedStatusDialog()

                }
            }

            donorName.typeface = fontUtil.title_font
            itemDuration.typeface = fontUtil.light_font
            itemAmount.typeface = fontUtil.regular_font
            itemCategory.typeface = fontUtil.title_font
            itemStatus.typeface = fontUtil.title_font

        }

        fun showDonatingStatusDialog() {
            val dialog = android.support.v7.app.AlertDialog.Builder(activity!!, R.style.MyDialogTheme)
                    .setTitle("Item Information")
                    .setMessage(status_donating)
                    .setIcon(R.drawable.ic_info_black_24dp)
                    .setNegativeButton("OK", {
                        _, _ ->
                    }).show()

            val message = dialog.findViewById(android.R.id.message) as TextView
            message.gravity = Gravity.CENTER
            message.setPadding(0,32,0,0)

        }

        fun showAcceptedStatusDialog() {
            val itemModel = itemList[position]
            firebaseDB.getReference("request_post")
                    .child(itemModel.accepted_charity_id)
                    .child(itemModel.donated_id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(requestSnapshot: DataSnapshot?) {
                            var requestName: String? = null

                            if (requestSnapshot != null) {
                                val requestModel=requestSnapshot.getValue(RequestModel::class.java)
                                if (fontFlag!!) {

                                    requestName = requestModel.request_place

                                } else {
                                    requestName = Rabbit.uni2zg( requestModel.request_place)
                                }
                                val dialog = android.support.v7.app.AlertDialog.Builder(activity!!, R.style.MyDialogTheme)
                                        .setTitle("Item Information")
                                        .setMessage(requestName + status_accepted)
                                        .setIcon(R.drawable.ic_info_black_24dp)
                                        .setPositiveButton("VIEW", DialogInterface.OnClickListener { dialog, which ->
                                            val i = Intent(activity, MainDonationActivity::class.java)
                                            i.putExtra("donationmodel", requestModel)
                                            i.putExtra("caller", "donor")
                                            activity!!.startActivity(i)

                                        })
                                        .setNegativeButton("OK", {
                                            _, _ ->
                                        })
                                        .show()

                                val message = dialog.findViewById(android.R.id.message) as TextView
                                message.gravity = Gravity.CENTER



                            }
                        }

                    })


        }

    }
}