package com.project.mt.dc

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.donor.activity.DonorProfileActivity
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseRemoveService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


/**
 * Created by mt on 8/2/17.
 */
class RecyclerMainDonationAdapter(donorList: ArrayList<NotificationModel>, requestModel: RequestModel,caller: String, activity: AppCompatActivity) : RecyclerView.Adapter<RecyclerMainDonationAdapter.donorListListViewHolder>() {

    var donorList = ArrayList<NotificationModel>()
    val firebaseRemoveService=FirebaseRemoveService()
    var activity: AppCompatActivity? = null
    var caller:String
    var currentUser:String?= null
    var requestModel: RequestModel

    val donorReference = FirebaseDatabase.getInstance().getReference("donor")
    val fontUtil=FontUtil(activity)
    var fontFlag:Boolean?= null

    init {
        this.donorList = donorList
        this.activity = activity
        this.requestModel = requestModel
        this.caller=caller
        MDetect.init(activity)
        fontFlag=MDetect.isUnicode()



        if(caller=="donor")
        {
            currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        }

    }

    override fun getItemCount(): Int {
        return donorList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): donorListListViewHolder {
        val v: View = LayoutInflater.from(parent!!.context).inflate(R.layout.listitem_maindonation, parent, false)
        var viewholder = donorListListViewHolder(v)
        return viewholder
    }

    override fun onBindViewHolder(holder: donorListListViewHolder?, position: Int) {

        val donorListModel = donorList!![position]


        if(caller =="donor"){
            if(currentUser!=donorListModel.donor_id){
                holder!!.img_options.visibility=View.GONE
            }
        }

        donorReference.child(donorListModel.donor_id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(donorSnapShot: DataSnapshot?) {
                        if (donorSnapShot != null) {

                            val donormodel = donorSnapShot.getValue(DonorInfoModel::class.java)
                            donorListModel.donor_model=donormodel
                            if(fontFlag!!) {
                                (holder)!!.donorName.text = donormodel.donor_name
                            }
                            else{
                                (holder)!!.donorName.text = Rabbit.uni2zg(donormodel.donor_name)
                            }
                            Glide.with(activity!!.applicationContext)
                                    .load(donormodel.donor_image)
                                    .placeholder(R.drawable.ic_person_black_24dp)
                                    .bitmapTransform(CropCircleTransformation(activity))
                                    .into((holder).donorImage)
                        }
                    }

                })
        if (holder != null) {
            if(fontFlag!!) {
                holder.lab_itemAmount.text = donorListModel.item_amount
                holder.lab_itemCategory.text = donorListModel.item_category
            }
            else{
                holder.lab_itemAmount.text = Rabbit.uni2zg(donorListModel.item_amount)
                holder.lab_itemCategory.text = Rabbit.uni2zg(donorListModel.item_category)
            }
        }


    }


    //view holder mode
    inner class donorListListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var donorName: TextView
        var donorImage: ImageView
        var lab_itemCategory:TextView
        var lab_itemAmount:TextView
        //var recycler_mainDonationItem:RecyclerView
        var img_options:ImageView
        var lab_donate:TextView

        init {

            donorName = itemView.findViewById(R.id.lab_donorname) as TextView
            lab_itemAmount = itemView.findViewById(R.id.lab_itemamount) as TextView
            lab_itemCategory = itemView.findViewById(R.id.lab_itemcategory) as TextView
            donorImage = itemView.findViewById(R.id.img_donorimage) as ImageView
            //recycler_mainDonationItem=itemView.findViewById(R.id.recycler_maindonationitems)as RecyclerView
            img_options=itemView.findViewById(R.id.img_options)as ImageView
            lab_donate=itemView.findViewById(R.id.lab_donate)as TextView

            //recycler_mainDonationItem.layoutManager=LinearLayoutManager(activity)

            itemView.setOnClickListener({

            })

            if(caller=="donated"){
                img_options.visibility=View.GONE
                lab_donate.text="donated"
            }
            else{
                lab_donate.text="donates"
            }
            img_options.setOnClickListener({
                if(caller=="donor") {
                    showDonorPopupMenu()
                }
                if(caller =="charity"){
                    showCharityPopupMenu()
                }
            })

            donorName.setOnClickListener({
                val donorListModel=donorList[position]
                val i=Intent(activity,DonorProfileActivity::class.java)
                i.putExtra("caller","visitor")
                i.putExtra("id",donorListModel.donor_id)
                activity!!.startActivity(i)
            })

        }

        fun showDonorPopupMenu() {
            val popup = PopupMenu(activity, img_options)
            popup.menuInflater.inflate(R.menu.popup_menu_donor_maindonation, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.popup_delete) {
                    AlertDialog.Builder(activity!!)
                            .setTitle("Comfirmation")
                            .setMessage("Do you really want to remove your donating item?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener {
                                dialog, whichButton ->
                                val donorListModel=donorList[position]
                                firebaseRemoveService.removeListItem("charity", requestModel.charity_id!!,"donor_list", requestModel.request_id!!, donorListModel.noti_id!!)
                                FirebaseWriteService().setItemStatus("donateitems", donorListModel.item_id!!, "item_status", "Donating", null)
                            })
                            .setNegativeButton(android.R.string.no, null).show()

                }
                true
            }
            popup.show()
        }

        fun showCharityPopupMenu(){
            val donorListModel=donorList[position]
            if(donorListModel.donor_name == null) {
                val dialogFragment = MainDonationDialogFragment(donorListModel,requestModel)
                dialogFragment.show(activity!!.supportFragmentManager, "Dialog Fragment")
            }
        }


    }
}
