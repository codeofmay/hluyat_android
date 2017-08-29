package com.project.mt.dc.charity.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

import com.project.mt.dc.R
import com.project.mt.dc.charity.activity.CharitySearchActivity
import com.project.mt.dc.charity.adapter.RecyclerDonationAdapter
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.FontUtil
import me.myatminsoe.mdetect.MDetect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {


    lateinit var txt_intro: TextView
    lateinit var recycler_charityDonation: RecyclerView
    lateinit var lab_upcoming: TextView
    lateinit var linear_search: LinearLayout
    var donationAdapter: RecyclerDonationAdapter? = null
    lateinit var fontUtil: FontUtil
    var fontFlag: Boolean? = null
    var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }


    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        FirebaseReadService().getCharityRequest(currentUser)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_charityhome, container, false)
        fontUtil = FontUtil(activity)

        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()

        txt_intro = v.findViewById(R.id.txt_charityintro) as TextView
        lab_upcoming = v.findViewById(R.id.lab_upcoming) as TextView
       // linear_search = v.findViewById(R.id.linear_search) as LinearLayout
        recycler_charityDonation = v.findViewById(R.id.recycler_charitydonations) as RecyclerView
        recycler_charityDonation.hasFixedSize()
        recycler_charityDonation.isNestedScrollingEnabled=false

        /*linear_search.setOnClickListener({
            val i = Intent(activity, CharitySearchActivity::class.java)
            startActivity(i)
        })*/
        recycler_charityDonation.layoutManager = LinearLayoutManager(activity)
        return v
    }

    @Subscribe
    fun getCharityRequest(listEvent: Event.ListEvent<RequestModel>) {
        if(listEvent.type=="requestlist") {
            if (listEvent.list!!.size == 0) {

                txt_intro.visibility = View.VISIBLE
                lab_upcoming.visibility = View.GONE

            } else {
                lab_upcoming.visibility = View.VISIBLE
                txt_intro.visibility = View.GONE

            }
            donationAdapter = RecyclerDonationAdapter(listEvent.list!!, activity, "charity")
            recycler_charityDonation.adapter = donationAdapter
            donationAdapter!!.notifyDataSetChanged()
        }
    }
}
