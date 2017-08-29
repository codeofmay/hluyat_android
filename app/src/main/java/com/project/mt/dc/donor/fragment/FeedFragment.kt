package com.project.mt.dc.donor.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.project.mt.dc.R
import com.project.mt.dc.donor.adapter.RecyclerDonateFeedAdapter
import com.project.mt.dc.event.Event
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.MethodUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class FeedFragment : Fragment() {

    var feedRecycler: RecyclerView? = null
    var feedAdapter: RecyclerDonateFeedAdapter? = null
    val firebaseReadService=FirebaseReadService()
    lateinit var lab_donorIntro: TextView

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)

    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_feed, container, false)

        feedRecycler = v.findViewById(R.id.recycler_donorfeed) as RecyclerView
        val swipeRecycler=v.findViewById(R.id.swiperefresh_donorfeed)as SwipeRefreshLayout
        lab_donorIntro=v.findViewById(R.id.txt_donorintro)as TextView

        val layoutManager: LinearLayoutManager = LinearLayoutManager(context)
        feedRecycler!!.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(feedRecycler!!.context,
                layoutManager.orientation)
        feedRecycler!!.addItemDecoration(dividerItemDecoration)

        feedRecycler!!.hasFixedSize()
        feedRecycler!!.setItemViewCacheSize(20)
        feedRecycler!!.isDrawingCacheEnabled = true
        feedRecycler!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        swipeRecycler.setOnRefreshListener {
            FirebaseReadService().getAllDonateItems("feed")
            swipeRecycler.isRefreshing=false
        }

        firebaseReadService.getAllDonateItems("feed")
        hasOptionsMenu()

        return v
    }


    @Subscribe
    fun getItemList(itemListEvent: Event.ItemListEvent) {
        val itemList = MethodUtil().reverse(itemListEvent.list!!)
        if(itemList.size == 0){
            lab_donorIntro.visibility=View.VISIBLE
        }
        else{
            lab_donorIntro.visibility=View.GONE
        }
        feedAdapter = RecyclerDonateFeedAdapter(itemList, activity)
        feedRecycler!!.adapter = feedAdapter
        feedAdapter!!.notifyDataSetChanged()

    }
}
