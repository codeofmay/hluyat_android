package com.project.mt.dc.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.model.NotificationModel

/**
 * Created by mt on 6/23/17.
 */
class FirebaseRemoveService : FirebaseService() {


    fun removeListItem(root: String, node: String) {
        FirebaseDatabase.getInstance().getReference(root).child(node).removeValue()
    }

    fun removeListItem(root: String, node: String, childNode: String) {
        FirebaseDatabase.getInstance().getReference(root).child(node).child(childNode).removeValue()
    }

    fun removeListItem(root: String, node: String, childNode: String,childNode1 :String) {
        FirebaseDatabase.getInstance().getReference(root).child(node).child(childNode).child(childNode1).removeValue()
    }
    fun removeListItem(root: String, node: String, childNode: String,childNode1 :String,childNode2: String) {
        val removeReference=FirebaseDatabase.getInstance().getReference(root).child(node).child(childNode).child(childNode1).child(childNode2)
        removeReference.removeValue()
    }

    fun removeMultipleNestedListItem(root: String, completeDonorList: ArrayList<NotificationModel>) {

        var i=0
        val reference=FirebaseDatabase.getInstance().getReference(root)
        //for each donor
        while (i<completeDonorList.size) {
            var j=0
            //for each item of donor
            val itemlist= completeDonorList[i].item_list
            if (itemlist != null) {
                while (j < itemlist.size) {
                    val model = itemlist[i]

                    reference.child(model.item_id).removeValue()
                    j++
                }
            }
            i++
        }
    }

    fun removeIndirectListItem(root: String,node: String, child: String,key: String, value: String) {
        FirebaseDatabase.getInstance().getReference(root).child(node).child(child).
                addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        for(dSnapshot: DataSnapshot in dataSnapshot!!.children){

                            if(dSnapshot.child(key).value == value){
                                dSnapshot.ref.removeValue()
                            }
                        }
                    }

                })
    }

    fun removeIndirectListItem(root: String,node: String,key: String, value: String) {
        FirebaseDatabase.getInstance().getReference(root).child(node).
                addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        for(dSnapshot: DataSnapshot in dataSnapshot!!.children){

                            if(dSnapshot.child(key).value == value){
                                dSnapshot.ref.removeValue()
                            }
                        }
                    }

                })
    }
}