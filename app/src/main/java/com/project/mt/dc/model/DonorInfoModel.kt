package com.project.mt.dc.model

import android.support.annotation.Keep
import java.io.Serializable

/**
 * Created by mt on 6/8/17.
 */
@Keep
class DonorInfoModel : Serializable{

    var donor_name: String? = null
    var donor_email: String? = null
    var donor_image: String? = null
    var donor_phone: String?= null
    var donor_city: String? = null
    var donor_township: String? = null
    var instance_id:String?=null
    var noti_count:String?= null
    var fb_id:String?= null
    var donor_bio:String?= null

    constructor(){

    }

    constructor(name: String, email : String,
                image: String, phone: String,
                city: String, township: String,instance_id: String){
        this.donor_name=name
        this.donor_email=email
        this.donor_image=image
        this.donor_phone=phone
        this.donor_city=city
        this.donor_township=township
        this.instance_id=instance_id
    }

    fun toHashMap() : HashMap<String,String>{
        val donorInfoHashMap = HashMap<String, String> ()
        donorInfoHashMap.put("donor_name", donor_name!!)
        donorInfoHashMap.put("donor_email", donor_email!!)
        donorInfoHashMap.put("donor_image", donor_image!!)
        donorInfoHashMap.put("donor_phone", donor_phone!!)
        donorInfoHashMap.put("donor_city", donor_city!!)
        donorInfoHashMap.put("donor_township", donor_township!!)
        donorInfoHashMap.put("noti_count", noti_count!!)
        donorInfoHashMap.put("instance_id", instance_id!!)
        return donorInfoHashMap
    }

    fun toEditHashMap() : HashMap<String,String>{
        val donorInfoHashMap = HashMap<String, String> ()
        donorInfoHashMap.put("donor_name", donor_name!!)
        donorInfoHashMap.put("donor_email", donor_email!!)
        donorInfoHashMap.put("donor_image", donor_image!!)
        donorInfoHashMap.put("donor_phone", donor_phone!!)
        donorInfoHashMap.put("donor_city", donor_city!!)
        donorInfoHashMap.put("donor_township", donor_township!!)
        donorInfoHashMap.put("noti_count", noti_count!!)
        donorInfoHashMap.put("donor_bio", donor_bio!!)
        donorInfoHashMap.put("instance_id", instance_id!!)
        return donorInfoHashMap
    }




}