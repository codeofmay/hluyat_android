package com.project.mt.dc.donor.adapter

import android.app.Activity
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
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit

/**
 * Created by mt on 8/19/17.
 */
class RecyclerCurrentDonors(donorList: ArrayList<DonorInfoModel>, activity: Activity) : RecyclerView.Adapter<RecyclerCurrentDonors.itemListViewHolder>() {

    var donoList = ArrayList<DonorInfoModel>()
    var activity: Activity? = null
    var fontUtil: FontUtil?= null
    var fontFlag:Boolean?=null
    val methodUtil=MethodUtil()

    init {
        this.donoList = donorList
        this.activity = activity
        fontUtil= FontUtil(activity)
        MDetect.init(activity)
        fontFlag= MDetect.isUnicode()

    }

    override fun getItemCount(): Int {
        return donoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): itemListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_currentdonors, parent, false)
        var viewholder = itemListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: itemListViewHolder?, position: Int) {

        val donorModel = donoList!![position]
        val donorName=methodUtil.splitName(donorModel.donor_name!!)

        if (fontFlag!!) {
            (holder)!!.donorName.text = donorName
        } else {
            (holder)!!.donorName.text = Rabbit.uni2zg(donorName)
        }

        Glide.with(activity!!.applicationContext).
                load(donorModel.donor_image)
                .placeholder(R.drawable.ic_person_black_24dp)
                .bitmapTransform(CropCircleTransformation(activity))
                .into((holder).donorImage)

    }


    //view holder model
    inner class itemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var donorName: TextView
        var donorImage: ImageView

        init {

            donorName = itemView.findViewById(R.id.lab_currentdonor) as TextView
            donorImage = itemView.findViewById(R.id.img_currentdonor) as ImageView

            donorName.typeface= fontUtil!!.regular_font


        }


    }
}
