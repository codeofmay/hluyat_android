package com.project.mt.dc.donor.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.project.mt.dc.R
import com.project.mt.dc.charity.activity.RequestFormActivity
import com.project.mt.dc.charity.adapter.RecyclerGridDonationAdapter
import com.project.mt.dc.donor.activity.DonateFormActivity
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.model.RequestModel


/**
 * Created by mt on 7/19/17.
 */
class BottonSheetFragment(activity: Activity, donationList: ArrayList<RequestModel>?, itemList: ArrayList<ItemInfoModel>?, caller: String) : BottomSheetDialogFragment() {

    val activity = activity
    val donationList = donationList
    val itemList = itemList
    val caller = caller
    var gridDonationAdapter: RecyclerGridDonationAdapter? = null
    lateinit var lab_add: TextView

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.bottonsheet_donation, null)
        dialog.setContentView(contentView)

        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        val recycler_charitydonation = contentView.findViewById(R.id.recycler_charitydonations) as RecyclerView
        recycler_charitydonation.layoutManager = GridLayoutManager(activity, 3)

        lab_add = contentView.findViewById(R.id.lab_add) as TextView
        val lab_title = contentView.findViewById(R.id.lab_title) as TextView

        val behavior = (contentView.parent as View).layoutParams
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }


        if (caller == "donor") {

            if (itemList != null) {
                if (itemList.size == 0) {
                    lab_title.text = "You have no donating item"
                    lab_add.visibility = View.VISIBLE

                    lab_add.setOnClickListener({
                        val i = Intent(activity, DonateFormActivity::class.java)
                        activity.startActivity(i)
                    })

                } else {
                    lab_add.visibility = View.GONE
                    lab_title.text = "Please select an item to donate"
                    gridDonationAdapter = RecyclerGridDonationAdapter(null, itemList, "donor", activity)
                    recycler_charitydonation.adapter = gridDonationAdapter
                    gridDonationAdapter!!.notifyDataSetChanged()
                }
            }

        } else if (caller == "charity") {

            if (donationList != null) {
                if (donationList.size == 0) {
                    lab_title.text = "You have no upcoming donation"
                    lab_add.visibility = View.VISIBLE

                    lab_add.setOnClickListener({
                        val i = Intent(activity, RequestFormActivity::class.java)
                        i.putExtra("caller","new")
                        activity.startActivity(i)
                    })


                } else {

                    lab_title.visibility = View.VISIBLE
                    lab_add.visibility = View.GONE

                    lab_title.text = "Please select a donation to request"
                    gridDonationAdapter = RecyclerGridDonationAdapter(donationList, null, "charity", activity)
                    recycler_charitydonation.adapter = gridDonationAdapter
                    gridDonationAdapter!!.notifyDataSetChanged()
                }
            }

        }


    }
}