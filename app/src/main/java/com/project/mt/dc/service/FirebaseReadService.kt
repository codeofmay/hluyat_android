package com.project.mt.dc.service



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.event.Event
import com.project.mt.dc.event.Event.*
import com.project.mt.dc.model.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by mt on 6/23/17.
 */
class FirebaseReadService : FirebaseService() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDB = FirebaseDatabase.getInstance()


    //Items
    fun getDonateItemByCategory(itemCategory: String) {

        val itemList = ArrayList<ItemInfoModel>()

        FirebaseDatabase.getInstance().getReference("donateitems").orderByChild("item_category").equalTo(itemCategory).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(itemSnapshot: DataSnapshot?) {

                itemList.clear()
                for (itemInfoSnapshot: DataSnapshot in itemSnapshot!!.children) {
                    val itemInfoModel = itemInfoSnapshot.getValue(ItemInfoModel::class.java)
                    itemList.add(itemInfoModel)

                }
                EventBus.getDefault().post(Event.ItemListEvent(itemList))

            }
        })
    }

    fun getTodayItem(todayDate: String) {
        val itemList = ArrayList<ItemInfoModel>()
        FirebaseDatabase.getInstance().getReference("donateitems").
                orderByChild("item_date").
                equalTo(todayDate).
                addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(itemSnapshot: DataSnapshot?) {
                        itemList.clear()
                        for (itemInfoSnapshot: DataSnapshot in itemSnapshot!!.children) {
                            val itemInfoModel = itemInfoSnapshot.getValue(ItemInfoModel::class.java)
                            itemList.add(itemInfoModel)
                        }

                        EventBus.getDefault().post(Event.ItemListEvent(itemList))
                    }

                })
    }

    fun getDonateItemByDonor(donorId: String) {

        val itemList = ArrayList<ItemInfoModel>()


        FirebaseDatabase.getInstance().getReference("donateitems").orderByChild("donor_id").equalTo(donorId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(itemSnapshot: DataSnapshot?) {

                        itemList.clear()
                        for (itemInfoSnapshot: DataSnapshot in itemSnapshot!!.children) {
                            val itemInfoModel = itemInfoSnapshot.getValue(ItemInfoModel::class.java)
                            itemList.add(itemInfoModel)

                        }
                        EventBus.getDefault().post(ListEvent("donoritemlist", itemList))
                    }
                })
    }

    fun getDonateItemByDonorGrid(donorId: String) {

        val itemList = ArrayList<ItemInfoModel>()


        FirebaseDatabase.getInstance().getReference("donateitems").orderByChild("donor_id").equalTo(donorId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(itemSnapshot: DataSnapshot?) {

                        itemList.clear()
                        for (itemInfoSnapshot: DataSnapshot in itemSnapshot!!.children) {
                            if (itemInfoSnapshot.child("item_status").value == "Donating") {
                                val itemInfoModel = itemInfoSnapshot.getValue(ItemInfoModel::class.java)
                                itemList.add(itemInfoModel)
                            }

                        }
                        EventBus.getDefault().post(ListEvent("donoritemgrid", itemList))
                    }
                })
    }


    fun getAllDonateItems(caller: String) {

        var itemList = ArrayList<ItemInfoModel>()
        val itemReference = FirebaseDatabase.getInstance().getReference("donateitems")
        if (caller == "feed") {
            itemReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(itemSnapshot: DataSnapshot?) {

                    itemList.clear()
                    for (itemInfoSnapshot: DataSnapshot in itemSnapshot!!.children) {

                        val itemInfoModel = itemInfoSnapshot.getValue(ItemInfoModel::class.java)
                        itemList.add(itemInfoModel)
                    }

                    EventBus.getDefault().post(Event.ItemListEvent(itemList))

                }
            })
        } else if (caller == "search") {
            itemReference.orderByChild("item_status").equalTo("Donating").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(itemSnapshot: DataSnapshot?) {

                    if (itemSnapshot != null) {
                        itemList.clear()
                        for (itemInfoSnapshot: DataSnapshot in itemSnapshot!!.children) {

                            val itemInfoModel = itemInfoSnapshot.getValue(ItemInfoModel::class.java)
                            itemList.add(itemInfoModel)
                        }

                        EventBus.getDefault().post(Event.SearchListEvent(itemList))

                    }
                }
            })

        }
    }


    //Donor
    fun getDonor(userid: String) {

        var donorModel: DonorInfoModel = DonorInfoModel()
        val donorReference = FirebaseDatabase.getInstance().getReference("donor").child(userid)
        donorReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(donorSnapshot: DataSnapshot?) {
                if (donorSnapshot != null) {
                    donorModel = donorSnapshot.getValue(DonorInfoModel::class.java)
                    EventBus.getDefault().post(Event.DonorModelEvent(donorModel))
                }

            }

            override fun onCancelled(p0: DatabaseError?) {

            }

        })
    }

    /*fun getDonorUpcoming(donorId: String){
        val reference=firebaseDB.getReference("donor")
                .child(donorId)
                .child("upcoming_donation")
        val requestReference=firebaseDB.getReference("request_post")
        val requestList=ArrayList<RequestModel>()
        reference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                requestList.clear()
                if (snapshot != null) {
                    for (rSnapshot:DataSnapshot in snapshot.children){
                        val requestId=rSnapshot.child("request_id").value.toString()
                        val charityId=rSnapshot.child("charity_id").value.toString()

                                requestReference.child(charityId)
                                .child(requestId)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError?) {

                                    }

                                    override fun onDataChange(requestSnapshot: DataSnapshot?) {
                                        requestList.clear()
                                        if (requestSnapshot != null) {

                                            val requestModel = requestSnapshot.getValue(RequestModel::class.java)
                                            requestList.add(requestModel)
                                        }
                                    }

                                })
                    }
                    eventBus.post(ListEvent("donor_upcoming",requestList))

                }
            }

        })
    }*/

    //Charity
    fun getCharity(charityId: String) {

        val charityReference = FirebaseDatabase.getInstance().getReference("charity")
                .child(charityId)

        charityReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(charitySnapshot: DataSnapshot?) {

                var charityModel = charitySnapshot!!.getValue(CharityModel::class.java)
                EventBus.getDefault().post(CharityModelEvent(charityModel))

            }

            override fun onCancelled(p0: DatabaseError?) {

            }

        })
    }

    fun getAllCharity() {
        val charityReference = FirebaseDatabase.getInstance().getReference("charity")
        val charityList = ArrayList<CharityModel>()
        charityReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(charitySnapshot: DataSnapshot?) {
                charityList.clear()
                if (charitySnapshot != null) {
                    for (cSnapshot: DataSnapshot in charitySnapshot.children) {
                        var charityModel = cSnapshot.getValue(CharityModel::class.java)
                        charityModel.charity_id = cSnapshot.key.toString()
                        charityList.add(charityModel)
                    }
                    EventBus.getDefault().post(ListEvent("charitylist", charityList))
                }


            }

            override fun onCancelled(p0: DatabaseError?) {

            }

        })
    }


    //Search
    fun Search(region: String, category: String) {
        val itemReference = FirebaseDatabase.getInstance().getReference("donateitems")
        val itemList = ArrayList<ItemInfoModel>()

        if (region != "All Region" && category != "All Category") {
            itemReference.orderByChild("donor_township").equalTo(region).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(itemSnapshot: DataSnapshot?) {
                    itemList.clear()
                    for (iSnapshot: DataSnapshot in itemSnapshot!!.children) {
                        if (iSnapshot.child("item_category").value.toString() == category) {
                            if (iSnapshot.child("item_status").value == "Donating") {
                                val itemInfoModel = iSnapshot.getValue(ItemInfoModel::class.java)
                                itemList.add(itemInfoModel)
                            }
                        }
                    }
                    EventBus.getDefault().post(Event.SearchListEvent(itemList))
                }

            })

        } else if (region != "All Region") {

            itemReference.orderByChild("donor_township").equalTo(region).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(itemSnapshot: DataSnapshot?) {
                    itemList.clear()
                    for (iSnapshot: DataSnapshot in itemSnapshot!!.children) {
                        if (iSnapshot.child("item_status").value == "Donating") {
                            val itemInfoModel = iSnapshot.getValue(ItemInfoModel::class.java)
                            itemList.add(itemInfoModel)
                        }
                    }
                    EventBus.getDefault().post(Event.SearchListEvent(itemList))
                }

            })
        } else if (category != "All Category") {
            itemReference.orderByChild("item_category").equalTo(category)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(itemSnapshot: DataSnapshot?) {
                            itemList.clear()
                            for (iSnapshot: DataSnapshot in itemSnapshot!!.children) {
                                if (iSnapshot.child("item_status").value == "Donating") {
                                    val itemInfoModel = iSnapshot.getValue(ItemInfoModel::class.java)
                                    itemList.add(itemInfoModel)
                                }
                            }
                            EventBus.getDefault().post(Event.SearchListEvent(itemList))
                        }

                    })
        } else if (category == "All Category" && region == "All Region") {
            getAllDonateItems("search")
        }
    }


    //Noti
    fun getNotificationById(rootName: String, id: String) {
        val notiIdList = ArrayList<NotificationModel>()

        val notiReference = FirebaseDatabase.getInstance().getReference("notification").child(rootName)

        notiReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(notiSnapshot: DataSnapshot?) {
                notiIdList.clear()
                for (nSnapshot: DataSnapshot in notiSnapshot!!.children) {
                    if (nSnapshot.child("to").value.toString() == id) {
                        val notiModel = nSnapshot.getValue(NotificationModel::class.java)
                        notiIdList.add(notiModel)
                    }
                }

                if (rootName == "to_charity") {
                    EventBus.getDefault().post(CharityNotiEvent(notiIdList))
                } else if (rootName == "to_donor") {
                    EventBus.getDefault().post(DonorNotiEvent(notiIdList))
                }
            }

        })

    }

    //Charity Request Post

    fun getAllRequest() {

        val requestList = ArrayList<RequestModel>()
        FirebaseDatabase.getInstance().getReference("request_post").
                addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(requestSnapshot: DataSnapshot?) {
                        requestList.clear()
                        if (requestSnapshot != null) {
                            for (rSnapShot: DataSnapshot in requestSnapshot.children) {
                                for (reqSnapShot: DataSnapshot in rSnapShot.children) {
                                    if(reqSnapShot.child("request_status").value.toString()=="requesting") {
                                        val requestModel = reqSnapShot.getValue(RequestModel::class.java)
                                        requestList.add(requestModel)
                                    }
                                }
                            }
                            EventBus.getDefault().post(ListEvent("requestlist", requestList))
                        }
                    }

                })
    }

    fun getCharityRequest(charityId: String) {

        val requestList = ArrayList<RequestModel>()
        FirebaseDatabase.getInstance().getReference("request_post").
                child(charityId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(requestSnapshot: DataSnapshot?) {
                        requestList.clear()
                        if (requestSnapshot != null) {
                            for (rSnapShot: DataSnapshot in requestSnapshot.children) {
                                if (rSnapShot.child("request_status").value.toString() == "requesting") {
                                    val requestModel = rSnapShot.getValue(RequestModel::class.java)
                                    requestList.add(requestModel)
                                }

                            }
                            EventBus.getDefault().post(ListEvent("requestlist", requestList))
                        }
                    }

                })
    }

    fun getCharityRequest(charityId: String, requestId: String,caller: String) {

        val requestList = ArrayList<RequestModel>()
        FirebaseDatabase.getInstance().getReference("request_post").
                child(charityId)
                .child(requestId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(requestSnapshot: DataSnapshot?) {
                        requestList.clear()
                        if (requestSnapshot != null) {

                            val requestModel = requestSnapshot.getValue(RequestModel::class.java)
                            EventBus.getDefault().post(RequestModelEvent(caller,requestModel))
                        }
                    }

                })
    }


    //Charity Request Post
    fun getCharityRequestCount(charityId: String) {

        FirebaseDatabase.getInstance().getReference("charity").
                child(charityId)
                .child("request_post").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(requestSnapshot: DataSnapshot?) {

                if (requestSnapshot != null) {
                    val count: String = requestSnapshot!!.childrenCount.toString()
                    EventBus.getDefault().post(StringEvent("requestcount", count))
                }
            }

        })
    }

    fun getCharityDonorList(charityId: String, requestId: String,caller:String) {
        val donorListReference = FirebaseDatabase.getInstance().getReference("charity").
                child(charityId).
                child("donor_list")
                .child(requestId)
        var donorList = ArrayList<NotificationModel>()

        donorListReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(donorListSnapshot: DatabaseError?) {

            }

            override fun onDataChange(donorListSnapshot: DataSnapshot?) {
                donorList.clear()
                for (dList: DataSnapshot in donorListSnapshot!!.children) {
                    val notiModel = dList.getValue(NotificationModel::class.java)
                    donorList.add(notiModel)
                }
                EventBus.getDefault().post(ListEvent(caller, donorList))

            }

        })

    }

    fun getCount(root: String, node: String) {
        val countReference = FirebaseDatabase.getInstance().getReference(root).child(node)

        countReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(countSnapshot: DataSnapshot?) {

                val count: String = countSnapshot!!.childrenCount.toString()
                EventBus.getDefault().post(StringEvent("requestcount", count))
            }

        })
    }


    fun getCount(root: String, node: String, childNode: String, caller: String) {
        val countReference = FirebaseDatabase.getInstance().getReference(root).child(node).child(childNode)

        countReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(countSnapshot: DataSnapshot?) {
                val count: String = countSnapshot!!.childrenCount.toString()
                EventBus.getDefault().post(StringEvent(caller, count))
            }

        })
    }

    fun getIndirectCount(root: String, key: String, value: String) {
        val countReference = FirebaseDatabase.getInstance().getReference(root)
        countReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(countSnapshot: DataSnapshot?) {
                if (countSnapshot != null) {
                    var count = 0
                    for (cSnapshot: DataSnapshot in countSnapshot.children) {
                        if (cSnapshot.child(key).value == value) {
                            count++
                        }
                    }
                    EventBus.getDefault().post(StringEvent("pending", count.toString()))
                }
            }

        })
    }

    fun getIndirectCount(root: String, child: String, key: String, value: String, caller: String) {
        val countReference = FirebaseDatabase.getInstance().getReference(root).child(child)
        var count = 0
        countReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(countSnapshot: DataSnapshot?) {
                count=0
                if (countSnapshot != null) {

                    for (cSnapshot: DataSnapshot in countSnapshot.children) {
                        if (cSnapshot.child(key).value == value) {
                            count++
                        }
                    }
                    EventBus.getDefault().post(StringEvent(caller, count.toString()))
                }
            }

        })
    }

    fun getCharityDonated(charityId: String) {
        val donatedItemList = ArrayList<RequestModel>()
        val requestPostReference = FirebaseDatabase.getInstance().getReference("request_post")
                .child(charityId)

        requestPostReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(requestSnapshot: DataSnapshot?) {
                if (requestSnapshot != null) {
                    donatedItemList.clear()

                    for (rSnapshot: DataSnapshot in requestSnapshot.children) {
                        if (rSnapshot.child("request_status").value.toString() == "done") {
                            val donatedItemModel = rSnapshot.getValue(RequestModel::class.java)
                            donatedItemList.add(donatedItemModel)
                        }
                    }
                    EventBus.getDefault().post(ListEvent("charity_donated_list", donatedItemList))

                }

            }

        })
    }

    fun getDonorDonated(id: String) {
        val donatedItemList = ArrayList<RequestModel>()
        val donatedItemReference = FirebaseDatabase.getInstance().getReference("donor")
                .child(id)
                .child("donated_item")
        val itemListReferene = donatedItemReference.child("item_list")
        donatedItemReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(donatedSnapshot: DataSnapshot?) {
                if (donatedSnapshot != null) {
                    donatedItemList.clear()
                    for (dSnapshot: DataSnapshot in donatedSnapshot.children) {
                        val donatedItemModel =dSnapshot.getValue(RequestModel::class.java)
                        donatedItemList.add(donatedItemModel)
                    }
                    EventBus.getDefault().post(ProfileListEvent(donatedItemList))
                }

            }
        })
    }

}