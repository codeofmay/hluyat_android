package com.project.mt.dc.model

import android.support.annotation.Keep
import java.io.Serializable

/**
 * Created by mt on 6/20/17.
 */
@Keep
class RequestModel : Serializable{

    var request_id: String?= null
    var request_location:String?= null
    var charity_id: String?= null
    var request_place:String?= null
    var request_date: String? =null
    var post_time:String?=null
    var request_description: String?= null
    var request_image: String?= null
    var request_status:String?= null
    var item_list: HashMap<String,ItemInfoModel>?=null



    fun toHashMap() : HashMap<String,String>{
        val requestInfoHashMap = HashMap<String, String> ()
        requestInfoHashMap.put("request_id", request_id!!)
        requestInfoHashMap.put("charity_id", charity_id!!)
        requestInfoHashMap.put("request_place", request_place!!)
        requestInfoHashMap.put("request_location", request_location!!)
        requestInfoHashMap.put("request_date", request_date!!)
        requestInfoHashMap.put("request_description", request_description!!)
        requestInfoHashMap.put("request_image", request_image!!)
        requestInfoHashMap.put("post_time", post_time!!)
        requestInfoHashMap.put("request_status", request_status!!)
        return requestInfoHashMap
    }

    fun todonorDonatedItemHashMap() : HashMap<String,String>{
        val notiInfoHashMap = HashMap<String, String> ()

        notiInfoHashMap.put("request_id", request_id!!)
        notiInfoHashMap.put("charity_id", charity_id!!)
        return notiInfoHashMap
    }


    fun tocharityDonatedItemHashMap() : HashMap<String,Any>{
        val notiInfoHashMap = HashMap<String, Any> ()
        notiInfoHashMap.put("done_id", request_id!!)
        notiInfoHashMap.put("noti_duration", post_time!!)
        notiInfoHashMap.put("done_place", request_place!!)
        notiInfoHashMap.put("done_description", request_description!!)
        notiInfoHashMap.put("done_image", request_image!!)
        notiInfoHashMap.put("done_location", request_location!!)
        notiInfoHashMap.put("done_date", request_date!!)


        return notiInfoHashMap
    }
}