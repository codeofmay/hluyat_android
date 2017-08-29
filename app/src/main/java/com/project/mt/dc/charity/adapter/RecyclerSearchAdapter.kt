package com.project.mt.dc.charity.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.project.mt.dc.R
import com.project.mt.dc.donor.activity.DonorProfileActivity
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


/**

 * Created by mt on 6/18/17.

 */

class RecyclerSearchAdapter(itemList: ArrayList<ItemInfoModel>, activity: Activity) : RecyclerView.Adapter<RecyclerSearchAdapter.SearchItemListViewHolder>() {

    var itemList = ArrayList<ItemInfoModel>()
    var activity: Activity? = null
    var r: RecyclerSearchAdapter.requestListener? = null
    val fontUtil = FontUtil(activity)
    var fontFlag:Boolean?=null


    interface requestListener {
        fun getrequestData(itemInfoModel: ItemInfoModel)
    }


    fun setRequsestListener(requestListener: RecyclerSearchAdapter.requestListener) {
        this.r = requestListener
    }

    init {
        this.itemList = itemList
        this.activity = activity
        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()

    }


    override fun getItemCount(): Int {
        return itemList.size

    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerSearchAdapter.SearchItemListViewHolder {

        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_requestfeed, parent, false)
        val viewholder: RecyclerSearchAdapter.SearchItemListViewHolder = SearchItemListViewHolder(v)

        return viewholder

    }


    override fun onBindViewHolder(holder: RecyclerSearchAdapter.SearchItemListViewHolder?, position: Int) {


        val itemModel = itemList[position]

        if (fontFlag!!) {
            (holder)!!.itemAmount.text = itemModel.item_amount
            (holder).itemCategory.text = itemModel.item_category

        } else {
            (holder)!!.itemAmount.text = Rabbit.uni2zg(itemModel.item_amount)
            (holder).itemCategory.text = Rabbit.uni2zg(itemModel.item_category)
        }

        (holder).itemDuration.text = itemModel.item_time
        Glide.with(activity!!.applicationContext)
                .load(itemModel.item_image)
                .into((holder).itemImage)

        val donorModel = itemModel.donor_model
        val itemDuration = MethodUtil().getTimeDifferent(itemModel.item_time!!)
        if (donorModel != null) {
            if (fontFlag!!) {
                (holder).donorTownship.text = donorModel.donor_township
                (holder).donorName!!.text = donorModel.donor_name
            } else {
                (holder).donorTownship.text = Rabbit.uni2zg(donorModel.donor_township)
                (holder).donorName!!.text = Rabbit.uni2zg(donorModel.donor_name)
            }
            Glide.with(activity!!.applicationContext)
                    .load(donorModel.donor_image)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .bitmapTransform(CropCircleTransformation(activity))
                    .into((holder).donorImage)
        }

        (holder).itemDuration.text = itemDuration


    }


    //view holder model

    inner class SearchItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemAmount: TextView
        var itemCategory: TextView
        var itemDuration: TextView
        var donorTownship: TextView
        var donorImage: ImageView
        var btnRequest: TextView
        var donorName: TextView
        var itemImage:ImageView


        init {

            itemAmount = itemView.findViewById(R.id.lab_sitemamount) as TextView
            itemCategory = itemView.findViewById(R.id.lab_sitemcategory) as TextView
            itemDuration = itemView.findViewById(R.id.lab_sitemduration) as TextView
            donorName = itemView.findViewById(R.id.lab_sdonorname) as TextView
            donorImage = itemView.findViewById(R.id.img_sdonorimage) as ImageView
            donorTownship = itemView.findViewById(R.id.lab_sdonortownship) as TextView
            btnRequest = itemView.findViewById(R.id.btn_request) as TextView
            itemImage=itemView.findViewById(R.id.img_itemimage)as ImageView

            donorName.typeface = fontUtil.title_font
            donorTownship.typeface=fontUtil.light_font
            itemDuration.typeface=fontUtil.light_font
            itemCategory.typeface=fontUtil.title_font
            itemAmount.typeface=fontUtil.regular_font

            donorName.setOnClickListener({

                val itemModel = itemList[position!!]
                val i = Intent(activity, DonorProfileActivity::class.java)
                i.putExtra("caller", "visitor")
                i.putExtra("id", itemModel.donor_id)
                activity!!.startActivity(i)
            })

            btnRequest.setOnClickListener({

                val itemModel = itemList[position!!]
                if (r != null) {
                    r!!.getrequestData(itemModel)
                }

            })

        }
    }
}