package com.project.mt.dc.charity.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.project.mt.dc.R
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.util.FontUtil
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus

/**
 * Created by mt on 7/31/17.
 */
class RecyclerGridDonationAdapter(donationList: ArrayList<RequestModel>?,itemList:ArrayList<ItemInfoModel>?,caller:String, activity: Activity) : RecyclerView.Adapter<RecyclerGridDonationAdapter.RequestListViewHolder>() {
    var donationList = ArrayList<RequestModel>()
    var activity: Activity? = null
    var caller:String
    var itemList=ArrayList<ItemInfoModel>()
    var fontFlag:Boolean?= null
    var fontUtil: FontUtil

    init {
        this.caller=caller

        if (caller=="donor"){
            this.itemList= itemList!!
        }
        if(caller=="charity"){
            this.donationList= donationList!!
        }
        this.activity=activity
        fontUtil= FontUtil(activity)
        MDetect.init(activity)
        fontFlag=MDetect.isUnicode()
    }

    override fun onBindViewHolder(holder: RequestListViewHolder?, position: Int) {
        if(caller =="charity") {
            val requestModel = donationList!![position]
            var place= requestModel.request_place
            var date=requestModel.request_date
            if (holder != null) {
                if(fontFlag!!) {
                    holder.lab_donationPlace.text =place
                    holder.lab_donationDate.text =date
                }
                else{
                    holder.lab_donationPlace.text =Rabbit.uni2zg(place)
                    holder.lab_donationDate.text =Rabbit.uni2zg(date)
                }
                Glide.with(activity)
                        .load(requestModel.request_image)
                        .into(holder.img_donationImage)

            }
        }
        else if(caller =="donor") {
            val itemModel = itemList!![position]
            var category= itemModel.item_category
            var amount=itemModel.item_amount
            if (holder != null) {
                if(fontFlag!!) {
                    holder.lab_donationPlace.text = category
                    holder.lab_donationDate.text = amount
                }
                else{
                    holder.lab_donationPlace.text =Rabbit.uni2zg(category)
                    holder.lab_donationDate.text = Rabbit.uni2zg(amount)
                }
                Glide.with(activity)
                        .load(itemModel.item_image)
                        .into(holder.img_donationImage)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RequestListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.griditem_charitydonation, parent, false)
        var viewholder = RequestListViewHolder(v)
        return viewholder
    }

    override fun getItemCount(): Int {
        if(caller =="charity") {
            return donationList.size
        }
        if(caller=="donor"){
            return itemList.size
        }
        return 0
    }

    //view holder model
    inner class RequestListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var img_donationImage: ImageView
        var lab_donationPlace:TextView
        var lab_donationDate:TextView


        init {
            img_donationImage = itemView.findViewById(R.id.img_donationImage) as ImageView
            lab_donationPlace=itemView.findViewById(R.id.lab_requestplace)as TextView
            lab_donationDate=itemView.findViewById(R.id.lab_requestdate)as TextView

            lab_donationPlace.typeface=fontUtil.regular_font
            lab_donationDate.typeface=fontUtil.regular_font

            itemView.setOnClickListener({
                if(caller =="charity") {
                    val requestModel = donationList[position]
                    EventBus.getDefault().post(Event.RequestModelEvent("gridcharityrequest",requestModel))
                }
                if(caller =="donor"){
                    val itemModel = itemList[position]
                    EventBus.getDefault().post(Event.ModelEvent("itemmodel",itemModel))
                }

            })

        }

    }
}
