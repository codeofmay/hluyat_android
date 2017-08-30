package com.project.mt.dc.service

import android.app.Activity
import android.content.DialogInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.R
import com.project.mt.dc.event.Event
import com.project.mt.dc.event.Event.AddressEvent
import com.project.mt.dc.model.TownshipModel
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus

/**
 * Created by mt on 6/11/17.
 */
class ShowAddressDialogService {
    val cityReference = FirebaseDatabase.getInstance().getReference("city")
    val townshipReference = FirebaseDatabase.getInstance().getReference("township")
    var cityList = ArrayList<String>()
    var cityIdList = ArrayList<String>()
    var cityArray: Array<String>? = null
    var townshipList = ArrayList<TownshipModel>()
    var context: Activity? = null
    var caller: String?= null

    constructor(caller: String,context: Activity){
        this.context = context
        this.caller=caller
        MDetect.init(context)
    }

    fun setData() {

        //city
        cityReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(addrSnapshot: DataSnapshot?) {
                cityList.clear()
                cityIdList.clear()
                if (caller == "search") {
                    cityList.add("All Region")
                    cityIdList.add("All Region")
                }
                for (citySnapShot: DataSnapshot in addrSnapshot!!.children) {

                    val cities = citySnapShot.child("name_mm").value.toString()
                    if (MDetect.isUnicode()) {
                        cityList.add(cities)
                    } else {
                        cityList.add(Rabbit.uni2zg(cities))
                    }
                    val cityId = citySnapShot.child("name").value.toString()
                    cityIdList.add(cityId)

                }
                cityArray = cityList.toArray(arrayOfNulls<String>(cityList.size))
            }
        })


        //township
        townshipReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(addrSnapshot: DataSnapshot?) {
                townshipList.clear()
                for (townshipSnapshot: DataSnapshot in addrSnapshot!!.children) {
                    val townshipModel = townshipSnapshot.getValue(TownshipModel::class.java)
                        townshipList.add(townshipModel)

                }
            }

        })
    }


    fun setCityDialog() {

        val cityDialog=android.support.v7.app.AlertDialog.Builder(context!!, R.style.ListDialogTheme)
                .setTitle("Citites")
        cityDialog.setItems(cityArray, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val chosenCity = cityArray!![which]
                val chosenCityId = cityIdList[which]
                if (chosenCity == "All Region") {
                    EventBus.getDefault().post(Event.SearchAddressEvent(chosenCity))
                } else {
                    if (caller == "charityprofile") {
                        EventBus.getDefault().post(Event.SearchAddressEvent(chosenCity))
                    } else {
                        setTownshipDialog(chosenCityId,chosenCity)
                    }
                }
            }

        })
        cityDialog.show()

    }


    fun setTownshipDialog(chosenCityId: String,chosenCity:String) {

        var i=0
        val filteredTownshipList=ArrayList<String>()
        filteredTownshipList.clear()
        while (i<townshipList.size){
            val townshipModel=townshipList[i]
            if(chosenCityId == townshipModel.state_id){
                if (MDetect.isUnicode()) {
                    filteredTownshipList.add(townshipModel.name_mm!!)
                }
                else{

                    filteredTownshipList.add(Rabbit.uni2zg(townshipModel.name_mm!!))
                }
            }
            i++
        }

        val townshipArray = filteredTownshipList.toArray(arrayOfNulls<String>(filteredTownshipList.size))
        val townshipDialog=android.support.v7.app.AlertDialog.Builder(context!!,R.style.ListDialogTheme)
                .setTitle("Townships")

        townshipDialog.setItems(townshipArray, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val chosenTownship = townshipArray[which].toString()
                EventBus.getDefault().post(AddressEvent(chosenCity, chosenTownship))

            }

        })
        townshipDialog.show()

    }

}