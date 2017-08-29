package com.project.mt.dc.donor.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.project.mt.dc.R
import com.project.mt.dc.donor.activity.DonateFormActivity
import com.project.mt.dc.donor.adapter.RecyclerDonateFeedAdapter
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class HomeFragment : Fragment() {
    var itemRecycler: android.support.v7.widget.RecyclerView? = null
    var adapter: RecyclerDonateFeedAdapter?  = null
    val currentUser= FirebaseAuth.getInstance().currentUser!!.uid
    val firebaseReadService=FirebaseReadService()


    lateinit var lab_donorIntro: TextView
    lateinit var lab_yourItem:TextView
    lateinit var swiperefresh_youritem:SwipeRefreshLayout
    lateinit var linear_search:LinearLayout


    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        firebaseReadService.getDonateItemByDonor(currentUser)

    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v= inflater!!.inflate(R.layout.fragment_donorfeed, container, false)

        val fontUtil=FontUtil(activity)

        itemRecycler=v.findViewById(R.id.recycler_donatefeed) as RecyclerView

        //linear_search=v.findViewById(R.id.linear_search)as LinearLayout

        lab_donorIntro=v.findViewById(R.id.txt_donorintro)as TextView
       // lab_yourItem=v.findViewById(R.id.lab_youritem)as TextView


        swiperefresh_youritem=v.findViewById(R.id.swiperefresh_youritem)as SwipeRefreshLayout

        val layoutManager: LinearLayoutManager = LinearLayoutManager(context)
        itemRecycler!!.layoutManager = layoutManager


        val dividerItemDecoration = DividerItemDecoration(itemRecycler!!.context,
                layoutManager.orientation)
        itemRecycler!!.addItemDecoration(dividerItemDecoration)

        itemRecycler!!.isNestedScrollingEnabled=false
        itemRecycler!!.hasFixedSize()
        itemRecycler!!.setItemViewCacheSize(20)
        itemRecycler!!.isDrawingCacheEnabled = true
        itemRecycler!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH


      /*  linear_search.setOnClickListener({
           val i= Intent(activity,DonorSearchActivity::class.java)
            startActivity(i)
        })
*/

        swiperefresh_youritem.setOnRefreshListener {
            firebaseReadService.getDonateItemByDonor(currentUser)
            swiperefresh_youritem.isRefreshing=false
        }



        return v
    }

    @Subscribe
    fun getItemList(itemListEvent: Event.ListEvent<ItemInfoModel>){

        if(itemListEvent.type=="donoritemlist") {
            val itemList = MethodUtil().reverse(itemListEvent.list!!)
            if (itemList.size == 0) {
                //lab_yourItem.visibility = View.GONE
                lab_donorIntro.visibility = View.VISIBLE
            } else {
                //lab_yourItem.visibility = View.VISIBLE
                lab_donorIntro.visibility = View.GONE
                adapter = RecyclerDonateFeedAdapter(itemList, activity)
                itemRecycler!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    fun showDonateForm(){
        val i= Intent(context, DonateFormActivity::class.java)
        startActivity(i)
    }


}
