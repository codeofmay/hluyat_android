package com.project.mt.dc


import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


/**
 * A simple [Fragment] subclass.
 */
open class MainDonationEditDialogFragment(donorListModel: NotificationModel, requestModel: RequestModel) : DialogFragment() {
    val donorListModel: NotificationModel = donorListModel
    val requestModel = requestModel
    lateinit var donorImage: ImageView
    lateinit var donorName: TextView
    lateinit var progressDialog:ProgressDialog
    lateinit var donorTownship: TextView
    lateinit var itemAmount: TextView
    lateinit var itemCategory: TextView
    lateinit var img_edit:ImageView
    lateinit var img_close:ImageView
    val firebaseDB=FirebaseDatabase.getInstance()

    var fontFlag: Boolean? = null


    override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        MDetect.init(activity)
        val fontUtil = FontUtil(activity)
        fontFlag = MDetect.isUnicode()
        val detailDialog = Dialog(activity!!)

        progressDialog= ProgressDialog(activity)
        progressDialog.setMessage("Please Wait")
        detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        detailDialog.setContentView(R.layout.dialog_main_donationedit)

        val params = detailDialog.window.attributes

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        detailDialog.window.attributes = params


        donorImage = detailDialog.findViewById(R.id.img_donorimage) as ImageView
        donorName = detailDialog.findViewById(R.id.lab_donorname) as TextView
        donorTownship = detailDialog.findViewById(R.id.lab_donortownship) as TextView
        itemAmount = detailDialog.findViewById(R.id.lab_itemamount) as TextView
        itemCategory = detailDialog.findViewById(R.id.lab_itemcategory) as TextView
        img_close=detailDialog.findViewById(R.id.img_close)as ImageView
        img_edit=detailDialog.findViewById(R.id.img_edit)as ImageView

        donorName.typeface = fontUtil.title_font
        donorTownship.typeface = fontUtil.regular_font
        itemAmount.typeface = fontUtil.regular_font
        itemCategory.typeface = fontUtil.regular_font

        setData()
        img_close.setOnClickListener({
            onDestroyView()
        })
        img_edit.setOnClickListener({
            progressDialog.show()
            var itemAmount=itemAmount.text.toString()
            if(!fontFlag!!){
                itemAmount= Rabbit.zg2uni(itemAmount)
            }
            firebaseDB.getReference("donateitems").child(donorListModel.item_id)
                    .child("item_amount")
                    .setValue(itemAmount)
            firebaseDB.getReference("charity").child(requestModel.charity_id)
                    .child("donor_list")
                    .child(requestModel.request_id)
                    .child(donorListModel.noti_id!!)
                    .child("item_amount")
                    .setValue(itemAmount)
            progressDialog.dismiss()
            detailDialog.dismiss()
            Toast.makeText(activity,"Item Amount has been edited",Toast.LENGTH_LONG).show()

        })

        return detailDialog
    }

    fun setData() {
        Glide.with(activity)
                .load(donorListModel.donor_model!!.donor_image)
                .placeholder(R.drawable.ic_person_black_24dp)
                .bitmapTransform(CropCircleTransformation(activity))
                .into(donorImage)

        if (fontFlag!!) {
            donorName.text = donorListModel.donor_model!!.donor_name
            donorTownship.text = donorListModel.donor_model!!.donor_township
            itemAmount.text = donorListModel.item_amount
            itemCategory.text = donorListModel.item_category
        } else {
            donorName.text = Rabbit.uni2zg(donorListModel.donor_model!!.donor_name)
            donorTownship.text = Rabbit.uni2zg(donorListModel.donor_model!!.donor_township)
            itemAmount.text = Rabbit.uni2zg(donorListModel.item_amount)
            itemCategory.text = Rabbit.uni2zg(donorListModel.item_category)
        }
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }
}
