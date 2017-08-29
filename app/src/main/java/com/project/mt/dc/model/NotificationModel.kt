package com.project.mt.dc.model

import android.support.annotation.Keep
import java.io.Serializable

/**
 * Created by mt on 6/20/17.
 */
@Keep
class NotificationModel : Serializable{

    var request_id: String?= null

    var charity_id: String? = null
    var charity_image: String?= null
    var charity_name: String?=null
    var charity_model: CharityModel?=null

    var item_category: String?= null
    var item_amount: String?= null
    var item_id: String?=null
    var item_image:String?= null
    var item_list: ArrayList<NotificationModel>?= null
    var item_model: ItemInfoModel?= null

    var noti_id: String? = null
    var noti_duration: String?= null
    var donate_duration: String?= null
    var noti_type: String? = null
    var noti_status:String?=null

    var to:String?= null
    var from:String?=null
    var donor_id: String?= null
    var donor_name: String?= null
    var donor_image: String?= null
    var donor_phone: String?= null
    var donor_township: String?= null
    var donor_model: DonorInfoModel?= null

    var done_id: String?=null
    var done_place: String? = null
    var done_description: String? = null
    var done_image: String? = null
    var donors: ArrayList<String>?= null
    var done_location:String?=null
    var done_date:String?=null
    //var donors: String?= null

    fun toCharityDonorlistHashMap() : HashMap<String,String>{
        val notiInfoHashMap = HashMap<String, String> ()

        notiInfoHashMap.put("noti_id", noti_id!!)
        notiInfoHashMap.put("donor_id", donor_id!!)
        notiInfoHashMap.put("item_id", item_id!!)
        notiInfoHashMap.put("item_amount", item_amount!!)
        notiInfoHashMap.put("item_category", item_category!!)
        notiInfoHashMap.put("noti_duration", noti_duration!!)
        return notiInfoHashMap
    }

    fun toCharityNotiHashMap() : HashMap<String,String>{
        val notiInfoHashMap = HashMap<String, String> ()

        notiInfoHashMap.put("noti_id", noti_id!!)
        notiInfoHashMap.put("from", donor_id!!)
        notiInfoHashMap.put("item_id", item_id!!)
        notiInfoHashMap.put("request_id", request_id!!)
        notiInfoHashMap.put("item_amount", item_amount!!)
        notiInfoHashMap.put("item_category", item_category!!)
        notiInfoHashMap.put("noti_duration", noti_duration!!)
        notiInfoHashMap.put("to", charity_id!!)
        return notiInfoHashMap
    }

    fun toDonorNotiHashMap() : HashMap<String,String>{
        val notiInfoHashMap = HashMap<String, String> ()
        notiInfoHashMap.put("noti_id", noti_id!!)
        notiInfoHashMap.put("from", charity_id!!)
        notiInfoHashMap.put("to", donor_id!!)
        notiInfoHashMap.put("request_id", request_id!!)
        notiInfoHashMap.put("item_id", item_id!!)
        notiInfoHashMap.put("item_amount", item_amount!!)
        notiInfoHashMap.put("item_category", item_category!!)
        notiInfoHashMap.put("noti_duration", noti_duration!!)
        notiInfoHashMap.put("noti_type", noti_type!!)
        return notiInfoHashMap
    }

    fun toDonorNotiDonatedHashMap() : HashMap<String,String>{
        val notiInfoHashMap = HashMap<String, String> ()
        notiInfoHashMap.put("noti_id", noti_id!!)
        notiInfoHashMap.put("from", charity_id!!)
        notiInfoHashMap.put("to", donor_id!!)
        notiInfoHashMap.put("request_id", request_id!!)
        notiInfoHashMap.put("item_amount", item_amount!!)
        notiInfoHashMap.put("noti_duration", noti_duration!!)
        notiInfoHashMap.put("noti_type", noti_type!!)
        return notiInfoHashMap
    }


    fun toCharityDonorListHashMap() : HashMap<String,String>{
        val donorListHashMap = HashMap<String, String> ()
        donorListHashMap.put("donor_id", donor_id!!)
        return donorListHashMap
    }



}