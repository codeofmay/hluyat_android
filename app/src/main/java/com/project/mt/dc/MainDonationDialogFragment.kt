package com.project.mt.dc


import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseRemoveService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


/**
 * A simple [Fragment] subclass.
 */
open class MainDonationDialogFragment(donorListModel: NotificationModel, requestModel: RequestModel) : DialogFragment() {
    val donorListModel: NotificationModel = donorListModel
    val requestModel = requestModel
    lateinit var donorImage: ImageView
    lateinit var donorName: TextView
    lateinit var donorTownship: TextView
    lateinit var itemAmount: TextView
    lateinit var itemCategory: TextView
    lateinit var img_call: ImageView
    lateinit var img_edit: ImageView
    lateinit var img_delete: ImageView
    lateinit var linear_options: LinearLayout
    var fontFlag: Boolean? = null


    override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        MDetect.init(activity)
        val fontUtil = FontUtil(activity)
        fontFlag = MDetect.isUnicode()
        val detailDialog = Dialog(activity!!)

        detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        detailDialog.setContentView(R.layout.dialog_main_donation)

        val params = detailDialog.window.attributes

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        detailDialog.window.attributes = params


        donorImage = detailDialog.findViewById(R.id.img_donorimage) as ImageView
        donorName = detailDialog.findViewById(R.id.lab_donorname) as TextView
        donorTownship = detailDialog.findViewById(R.id.lab_donortownship) as TextView
        itemAmount = detailDialog.findViewById(R.id.lab_itemamount) as TextView
        itemCategory = detailDialog.findViewById(R.id.lab_itemcategory) as TextView

        img_call = detailDialog.findViewById(R.id.img_call) as ImageView
        img_edit = detailDialog.findViewById(R.id.img_edit) as ImageView
        img_delete = detailDialog.findViewById(R.id.img_delete) as ImageView
        linear_options = detailDialog.findViewById(R.id.linear_options) as LinearLayout

        donorName.typeface = fontUtil.title_font
        donorTownship.typeface = fontUtil.regular_font
        itemAmount.typeface = fontUtil.regular_font
        itemCategory.typeface = fontUtil.regular_font

        setData()

        img_call.setOnClickListener({
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + donorListModel.donor_model!!.donor_phone)
            startActivity(intent)
        })

        img_delete.setOnClickListener({
            detailDialog.dismiss()
            AlertDialog.Builder(activity!!)
                    .setTitle("Comfirmation")
                    .setMessage("Do you really want to remove this donor?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener {
                        dialog, whichButton ->

                        FirebaseRemoveService().removeListItem("charity", requestModel.charity_id!!, "donor_list", requestModel.request_id!!, donorListModel.noti_id!!)
                        FirebaseWriteService().setItemStatus("donateitems", donorListModel.item_id!!, "item_status", "Donating", null)


                    })
                    .setNegativeButton(android.R.string.no, null).show()

        })

        img_edit.setOnClickListener({
            onDestroyView()
            val dialogFragment = MainDonationEditDialogFragment(donorListModel,requestModel)
            dialogFragment.show(activity!!.supportFragmentManager, "Dialog Fragment")

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
