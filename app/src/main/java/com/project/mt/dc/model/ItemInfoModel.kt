package com.project.mt.dc.model

import android.support.annotation.Keep
import java.io.Serializable

/**
 * Created by mt on 6/12/17.
 */
@Keep
class ItemInfoModel : Serializable {
    var item_key: String? = null
    var item_amount: String?=null
    var item_category:String?= null
    var item_image:String?=null
    var donor_id: String?=null
    var item_time: String? = null
    var item_date:String? = null
    var donor_model:DonorInfoModel?=null
    var item_status: String? = null
    var accept_notiId: String?= null
    var donor_township:String?= null
    var donated_id:String?= null
    var accepted_charity_id:String?= null


    fun toHashMap() : HashMap<String,String>{
        val itemInfoHashMap = HashMap<String, String> ()
        itemInfoHashMap.put("item_key", item_key!!)
        itemInfoHashMap.put("item_category", item_category!!)
        itemInfoHashMap.put("item_amount", item_amount!!)
        itemInfoHashMap.put("item_image", item_image!!)
        itemInfoHashMap.put("item_time", item_time!!)
        itemInfoHashMap.put("item_date", item_date!!)
        itemInfoHashMap.put("donor_id", donor_id!!)
        itemInfoHashMap.put("donor_township", donor_township!!)
        itemInfoHashMap.put("item_status", item_status!!)
        return itemInfoHashMap
    }

    fun toItemListHashMap() : HashMap<String,String>{
        val itemListHashMap = HashMap<String, String> ()

        itemListHashMap.put("item_amount", item_amount!!)
        itemListHashMap.put("item_category", item_category!!)
        return itemListHashMap
    }


}