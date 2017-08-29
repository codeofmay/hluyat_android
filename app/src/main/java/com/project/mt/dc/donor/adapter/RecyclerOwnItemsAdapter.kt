package com.project.mt.dc.donor.adapter

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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
import com.project.mt.dc.R.id.lab_itemamount
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseRemoveService
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


class RecyclerOwnItemsAdapter(itemList: ArrayList<ItemInfoModel>, currentUser: String, caller: String, activity: Activity) : RecyclerView.Adapter<RecyclerOwnItemsAdapter.UserItemListViewHolder>() {

    var itemList = ArrayList<ItemInfoModel>()
    var activity: Activity? = null
    lateinit var currentUser: String
    lateinit var caller: String
    val firebaseDB=FirebaseDatabase.getInstance()
    var fontUtil: FontUtil? = null
    var string_please_remove: String? = null
    var string_this_item: String? = null
    var fontFlag: Boolean? = null

    init {
        this.itemList = itemList
        this.activity = activity
        this.currentUser = currentUser
        this.caller = caller

        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()
        fontUtil = FontUtil(activity)

        fontFlag = MDetect.isUnicode()
        if (fontFlag!!) {
            string_this_item=activity.getString(R.string.this_item)
            string_please_remove = activity.getString(R.string.please_remove)
        } else {
            string_this_item=Rabbit.uni2zg(activity.getString(R.string.this_item))
            string_please_remove = Rabbit.uni2zg(activity.getString(R.string.please_remove))
        }
    }

    override fun getItemCount(): Int {
        return itemList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerOwnItemsAdapter.UserItemListViewHolder {

        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_ownitems, parent, false)
        var viewholder: RecyclerOwnItemsAdapter.UserItemListViewHolder = UserItemListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: RecyclerOwnItemsAdapter.UserItemListViewHolder?, position: Int) {

        var viewHolder = holder as RecyclerOwnItemsAdapter.UserItemListViewHolder

        val itemInfoModel = itemList!![position]
        val itemDuration = MethodUtil().getTimeDifferent(itemInfoModel.item_time!!)
        (holder).itemDuration.text = itemDuration
        (holder).itemStatus.text = itemInfoModel.item_status

        if (fontFlag!!) {
            (holder).itemAmount.text = itemInfoModel.item_amount
            (holder).itemCategory.text = itemInfoModel.item_category
        }
        else{
            (holder).itemAmount.text = Rabbit.uni2zg(itemInfoModel.item_amount)
            (holder).itemCategory.text = Rabbit.uni2zg(itemInfoModel.item_category)
        }

        if (itemInfoModel.item_status == "Donating") {
            holder.itemStatus!!.text="onating"
            holder.relative_pending.visibility=View.VISIBLE
            holder.relative_donated.visibility=View.GONE
        }
        if (itemInfoModel.item_status == "Accepted") {
            holder.itemStatus!!.text="ccepted"
            holder.relative_pending.visibility=View.GONE
            holder.relative_donated.visibility=View.VISIBLE
        }
        Glide.with(activity)
                .load(itemInfoModel.item_image)
                .into((holder).itemImage)

        val donorReference = FirebaseDatabase.getInstance().getReference("donor").child(currentUser)

        donorReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot?) {
                val donorModel: DonorInfoModel = datasnapshot!!.getValue(DonorInfoModel::class.java)

                if(fontFlag!!) {
                    (holder).donorName.text = donorModel.donor_name
                }
                else{
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
    inner class UserItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemAmount: TextView
        var itemCategory: TextView
        var itemImage: ImageView
        var itemDuration: TextView
        var donorName: TextView
        var donorImage: ImageView
        var itemStatus: TextView
        var img_deleteItem: ImageView
        var relative_donated:RelativeLayout
        var relative_pending:RelativeLayout


        init {

            itemAmount = itemView.findViewById(lab_itemamount) as TextView
            itemCategory = itemView.findViewById(R.id.lab_itemcategory) as TextView
            itemDuration = itemView.findViewById(R.id.lab_duration) as TextView
            itemImage = itemView.findViewById(R.id.img_itemimage) as ImageView
            donorName = itemView.findViewById(R.id.lab_donorname) as TextView
            donorImage = itemView.findViewById(R.id.img_donorImage) as ImageView
            itemStatus = itemView.findViewById(R.id.lab_itemstatus) as TextView
            img_deleteItem = itemView.findViewById(R.id.img_deleteitem) as ImageView
            relative_donated = itemView.findViewById(R.id.relative_donated) as RelativeLayout
            relative_pending = itemView.findViewById(R.id.relative_pending) as RelativeLayout

            itemAmount.typeface = fontUtil!!.regular_font
            itemCategory.typeface = fontUtil!!.medium_font
            donorName.typeface = fontUtil!!.title_font
            itemDuration.typeface = fontUtil!!.light_font



            if (caller == "visitor") {
                img_deleteItem.visibility = GONE
            }
            img_deleteItem.setOnClickListener({
                val itemModel = itemList[position!!]
                val userID = FirebaseAuth.getInstance().currentUser!!.uid
                if (itemModel.item_status=="Accepted"){
                    showAcceptedStatusDialog()
                }
                if (itemModel.item_status=="Donating"){
                    showWarningDialog()
                }
            })
        }


        fun showWarningDialog() {
            val itemModel = itemList[position!!]
            android.support.v7.app.AlertDialog.Builder(activity!!)
                    .setTitle("Comfirmation")
                    .setMessage("Do you really want to remove this item?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener {
                        dialog, whichButton ->
                        itemList.removeAt(position!!)
                        FirebaseRemoveService().removeListItem("donateitems", itemModel.item_key!!)
                        FirebaseRemoveService().removeIndirectListItem("notification","to_donor","item_id", itemModel.item_key!!)
                        notifyDataSetChanged()

                    })
                    .setNegativeButton(android.R.string.no, null).show()
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
                                        .setMessage(string_this_item+requestName + string_please_remove)
                                        .setIcon(android.R.drawable.ic_dialog_info)
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

                                val negativeBtn = dialog.findViewById(android.R.id.button1) as Button
                                negativeBtn.setTextColor(Color.parseColor("#2196f3"))

                                val positiveBtn = dialog.findViewById(android.R.id.button2) as Button
                                positiveBtn.setTextColor(Color.parseColor("#2196f3"))
                            }
                        }

                    })


        }


    }
}