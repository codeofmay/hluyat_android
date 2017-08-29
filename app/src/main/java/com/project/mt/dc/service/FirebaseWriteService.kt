package com.project.mt.dc.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.util.MethodUtil


/**
 * Created by mt on 6/23/17.
 */
class FirebaseWriteService : FirebaseService() {
    val methodUtil = MethodUtil()
    val firebaseDB = FirebaseDatabase.getInstance()
    val firebaseRemoveService= FirebaseRemoveService()

    //Item
    fun setDonateItem(itemModel: ItemInfoModel) {
        FirebaseDatabase.getInstance().getReference("donateitems").child(itemModel.item_key).setValue(itemModel.toHashMap())

    }


    //noti
    fun setCharityRequest(charityId: String, request_id: String, requestModel: RequestModel) {
        val requestReference = FirebaseDatabase.
                getInstance().
                getReference("request_post").
                child(charityId)
                .child(request_id)
        requestReference.setValue(requestModel.toHashMap())
    }


    fun setNotification(rootName: String, notiModel: NotificationModel) {
        var notiReference = FirebaseDatabase.getInstance().getReference("notification")
        var key = notiReference.push().key

        notiModel.noti_id = key
        if (rootName == "charity") {
            notiReference.child("to_charity").child(key).setValue(notiModel.toCharityNotiHashMap())
        }
        if (rootName == "donor") {
            if(notiModel.noti_type=="request") {
                notiReference.child("to_donor").child(key).setValue(notiModel.toDonorNotiHashMap())
            }
            if(notiModel.noti_type == "done"){
                notiReference.child("to_donor").child(key).setValue(notiModel.toDonorNotiDonatedHashMap())
            }
        }

    }

    fun setDonorList(charityId: String, notiModel: NotificationModel) {

        val donorNotiKey = notiModel.noti_id
        val donorListReference = firebaseDB.getReference("charity").
                child(charityId).child("donor_list")
                .child(notiModel.request_id)

        val notiReference = firebaseDB.getReference("notification").child("to_charity")

        val donorReference = firebaseDB.getReference("donor")
                .child(notiModel.donor_id)
                .child("upcoming_donation")
        val key = donorListReference.push().key
        notiModel.noti_id = key
        notiModel.noti_duration = methodUtil.getCurrentTime()

        //setdonorlist
        donorListReference.child(key).setValue(notiModel.toCharityDonorlistHashMap())
        //setnoti
        notiReference.child(key).setValue(notiModel.toCharityNotiHashMap())
        //setupcoming
        val hashmap = HashMap<String, String>()
        hashmap.put("request_id", notiModel.request_id!!)
        hashmap.put("charity_id", charityId)
        donorReference.child(key).setValue(hashmap)

        val reference = FirebaseDatabase.getInstance().getReference("donateitems").
                child(notiModel.item_id)
        val childMap = HashMap<String, Any>()
        childMap.put("item_status", "Accepted")
        childMap.put("donated_id", notiModel.request_id!!)
        if(donorNotiKey!= null) {
            childMap.put("accept_notiId", donorNotiKey)
        }
        childMap.put("accepted_charity_id", notiModel.charity_id!!)
        reference.updateChildren(childMap)


    }


    fun setDonatedItem(donorList:ArrayList<NotificationModel>,doneModel: RequestModel) {

        val donorReference = FirebaseDatabase.getInstance().getReference("donor")
        val donorIdList=ArrayList<String>()
        donorIdList.clear()

        //set charity donated
        val charityReference = FirebaseDatabase.getInstance().getReference("request_post")
                .child(doneModel.charity_id)
                .child(doneModel.request_id)
        charityReference.setValue(doneModel.toHashMap())

        //set donor donated
        var i = 0
        while (i < donorList.size) {
            val donorListModel = donorList[i]
            val donorReference = donorReference.child(donorListModel.donor_id).child("donated_item").child(doneModel.request_id)
            val itemReference=donorReference.child("item_list")
            donorReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(donordonatedSnapshot: DataSnapshot?) {
                    if (donordonatedSnapshot != null ) {
                        if(donorIdList.contains(donorListModel.donor_id)) {

                            //just add item
                            val itemModel = ItemInfoModel()
                            itemModel.item_amount = donorListModel.item_amount
                            itemModel.item_category = donorListModel.item_category

                            itemReference.child(donorListModel.item_id)
                                    .setValue(itemModel.toItemListHashMap())
                            //remove donating item
                            firebaseRemoveService.removeListItem("donateitems", donorListModel.item_id!!)

                            //remove notification related with item
                            firebaseRemoveService.removeIndirectListItem("notification","to_donor","item_id", donorListModel.item_id!!)


                        }
                        else {

                            //add id info
                            donorReference.setValue(doneModel.todonorDonatedItemHashMap())

                            //add item info
                            val itemModel = ItemInfoModel()
                            itemModel.item_amount = donorListModel.item_amount
                            itemModel.item_category = donorListModel.item_category

                            itemReference.child(donorListModel.item_id)
                                    .setValue(itemModel.toItemListHashMap())

                            //remove donating item
                            firebaseRemoveService.removeListItem("donateitems", donorListModel.item_id!!)
                            //remove notification related with item
                            firebaseRemoveService.removeIndirectListItem("notification","to_donor","item_id", donorListModel.item_id!!)

                            //set noti
                            val notiModel = NotificationModel()
                            notiModel.charity_id = doneModel.charity_id
                            notiModel.request_id = doneModel.request_id
                            notiModel.donor_id = donorListModel.donor_id
                            notiModel.item_amount = itemModel.item_amount
                            notiModel.noti_type = "done"
                            notiModel.noti_duration = MethodUtil().getCurrentTime()

                            //set Notification to donor
                            setNotification("donor", notiModel)
                        }
                        donorIdList.add(donorListModel.donor_id!!)

                    }

                }

            })
            i++
        }

    }

    fun setItemStatus(root: String, node: String, key: String, value: String, accept_notiId: String?) {
        val reference = FirebaseDatabase.getInstance().getReference(root).
                child(node)
        val childMap = HashMap<String, Any>()
        childMap.put(key, value)
        if(accept_notiId!=null) {
            childMap.put("accept_notiId", accept_notiId)
        }
        reference.updateChildren(childMap)
    }

    fun setStatus(root: String,sub:String,node: String,key: String,value: String){
        val reference=FirebaseDatabase.getInstance().getReference(root)
                .child(sub).
                child(node)
        val childMap = HashMap<String, Any>()
        childMap.put(key, value)
        reference.updateChildren(childMap)
    }

    fun updateChild(root: String, node: String, hashmap: Map<String, String>) {
        val updateReference = FirebaseDatabase.getInstance().getReference(root).child(node)
        updateReference.updateChildren(hashmap)
    }

    fun updateChildIndirect(root: String, searchkey: String, searchvalue: String, updatekey: String, updatevalue: String) {

        //it will update donor_township
        if (root == "donateitems") {
            val databaseReference = FirebaseDatabase.getInstance().getReference(root)
            databaseReference.orderByChild(searchkey)
                    .equalTo(searchvalue)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(itemSnapshot: DataSnapshot?) {
                            if (itemSnapshot != null) {
                                for (iSnapshot: DataSnapshot in itemSnapshot.children) {
                                    databaseReference.child(iSnapshot.key).child(updatekey).setValue(updatevalue)
                                }
                            }
                        }

                    })
        }
    }

}