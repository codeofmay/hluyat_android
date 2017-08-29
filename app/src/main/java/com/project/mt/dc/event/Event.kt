package com.project.mt.dc.event

import com.project.mt.dc.model.*

/**
 * Created by mt on 6/18/17.
 */
class Event {

    class AddressEvent {
        var city: String?= null
        var township: String?= null

        constructor(city:String?,township:String){
            this.city=city
            this.township=township
        }


    }
    class CharityLoginFlagEvent {
        var flag: Boolean?= null
        constructor(flag:Boolean){
            this.flag=flag
        }
    }
    class CharityModelEvent {
        var charityModel: CharityModel?= null
        constructor(charityModel: CharityModel)
        {
            this.charityModel=charityModel
        }
    }
    class CharityNotiEvent {
        var list: ArrayList<NotificationModel>?= null
        constructor(list:ArrayList<NotificationModel>){
            this.list=list
        }
    }

    class ListEvent<T>{
        var list: ArrayList<T>?= null
        var type: String?= null
        constructor(type: String,list: ArrayList<T>){
            this.list=list
            this.type=type
        }
    }

    class ModelEvent<T>{
        var model: T?= null
        var type: String?= null
        constructor(type: String,model:T){
            this.model=model
            this.type=type
        }
    }
    class DonorListEvent {
        var list: ArrayList<NotificationModel>?= null
        constructor(list:ArrayList<NotificationModel>){
            this.list=list
        }
    }
    class ItemListEvent {
        var list: ArrayList<ItemInfoModel>? = null
        constructor(list: ArrayList<ItemInfoModel>)
        {
            this.list=list
        }
    }
    class DonorNotiEvent {
        var list: ArrayList<NotificationModel>?= null
        constructor(list:ArrayList<NotificationModel>){
            this.list=list
        }
    }
    class DonorModelEvent {
        var donorModel: DonorInfoModel?= null
        constructor(donorModel: DonorInfoModel)
        {
            this.donorModel=donorModel
        }
    }
    class ProfileListEvent {
        var list: ArrayList<RequestModel>?= null
        constructor(list:ArrayList<RequestModel>){
            this.list=list
        }
    }
    class RequestModelEvent {
        var caller:String?= null
        var requestModel: RequestModel?= null
        constructor(caller:String,requestModel: RequestModel){
            this.caller=caller
            this.requestModel= requestModel
        }
    }

    class RequestListEvent {
        var list: ArrayList<RequestModel>? = null
        constructor(list: ArrayList<RequestModel>)
        {
            this.list=list
        }
    }
    class SearchAddressEvent {
        var township: String?= null

        constructor(township:String){
            this.township=township
        }
    }
    class SearchCategoryEvent {
        var category:String?=null
        constructor(category: String){
            this.category=category
        }
    }
    class SearchListEvent {
        var list:ArrayList<ItemInfoModel>?= null
        constructor(list:ArrayList<ItemInfoModel>){
            this.list=list
        }
    }
    class StringEvent {
        var type:String?= null
        var string: String? = null
        constructor(type: String,string:String){
            this.string=string
            this.type=type
        }
    }
    class UserItemListEvent {
        var list: ArrayList<ItemInfoModel>? = null

        constructor(list: ArrayList<ItemInfoModel>)
        {
            this.list=list
        }
    }

}