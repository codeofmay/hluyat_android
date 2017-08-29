package com.project.mt.dc.service

import android.content.Context
import android.content.DialogInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.R
import com.project.mt.dc.event.Event
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus

/**
 * Created by mt on 6/19/17.
 */
class ShowCategoryDialogService {
    var context:Context?= null
    var categoryList= ArrayList<String>()
    var caller:String?=null
    constructor(context: Context,caller:String){
        this.context=context
        this.caller=caller
        MDetect.init(context!!)
        setCategoryDialog()
    }

    fun setCategoryDialog(){

        FirebaseDatabase.getInstance().getReference("itemcategory").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(categorySnapshot: DataSnapshot?) {

                categoryList!!.clear()
                if (caller == "search") {
                    categoryList.add("All Category")
                }
                for (cSnapShot: DataSnapshot in categorySnapshot!!.children) {

                    var category: String?= null
                    if (MDetect.isUnicode()) {
                         category= cSnapShot.child("cname").value.toString()
                    }
                    else{
                        category=Rabbit.uni2zg(cSnapShot.child("cname").value.toString())
                    }

                    if (category != null) {
                        categoryList!!.add(category)
                    }

                }



                val categoryArray = categoryList!!.toArray(arrayOfNulls<String>(categoryList!!.size))

                val categoryDialog=android.support.v7.app.AlertDialog.Builder(context!!,R.style.MyDialogTheme)
                        .setTitle("Categories")

                categoryDialog.setItems(categoryArray, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val chosenCategory = categoryArray[which].toString()
                        EventBus.getDefault().post(Event.SearchCategoryEvent(chosenCategory))
                    }

                })

                categoryDialog.show()

            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })

    }


}