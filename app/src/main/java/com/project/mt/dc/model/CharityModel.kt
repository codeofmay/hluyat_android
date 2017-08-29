package com.project.mt.dc.model

import android.support.annotation.Keep
import java.io.Serializable

/**
 * Created by mt on 6/21/17.
 */
@Keep
class CharityModel : Serializable{
    var charity_id: String?= null
    var charity_city: String?= null
    var charity_description: String?= null
    var charity_email: String?= null
    var charity_image: String?= null
    var charity_name: String?= null
    var charity_password: String?= null
    var charity_phone: String?= null
    var charity_year: String?= null
    var request_post: HashMap<String,Any>? = null
    var donated_item: HashMap<String,Any>?= null
    var notification:HashMap<String,Any>?= null
    var instance_id:String?= null
    var noti_count:String?= null


    var request_id: String?= null

    fun toHashMap() : HashMap<String,String>{
        val charityInfoHashMap = HashMap<String, String> ()
        charityInfoHashMap.put("charity_city", charity_city!!)
        charityInfoHashMap.put("charity_description", charity_description!!)
        charityInfoHashMap.put("charity_email", charity_email!!)
        charityInfoHashMap.put("charity_image", charity_image!!)
        charityInfoHashMap.put("charity_name", charity_name!!)
        charityInfoHashMap.put("charity_password", charity_password!!)
        charityInfoHashMap.put("charity_phone", charity_phone!!)
        charityInfoHashMap.put("charity_year", charity_year!!)
        charityInfoHashMap.put("instance_id", instance_id!!)

        return charityInfoHashMap
    }

    fun toEditHashMap() : HashMap<String,String>{
        val charityInfoHashMap = HashMap<String, String> ()

        charityInfoHashMap.put("charity_description", charity_description!!)
        charityInfoHashMap.put("charity_image", charity_image!!)
        charityInfoHashMap.put("charity_name", charity_name!!)
        charityInfoHashMap.put("charity_password", charity_password!!)
        charityInfoHashMap.put("charity_phone", charity_phone!!)
        charityInfoHashMap.put("charity_year", charity_year!!)

        return charityInfoHashMap
    }
    fun toRequestHashMap() : HashMap<String,String>{
        val charityInfoHashMap = HashMap<String, String> ()
        charityInfoHashMap.put("request_id", request_id!!)
        charityInfoHashMap.put("charity_city", charity_city!!)
        charityInfoHashMap.put("charity_email", charity_email!!)
        charityInfoHashMap.put("charity_name", charity_name!!)
        charityInfoHashMap.put("charity_phone", charity_phone!!)
        charityInfoHashMap.put("charity_image", charity_image!!)
        return charityInfoHashMap
    }
}