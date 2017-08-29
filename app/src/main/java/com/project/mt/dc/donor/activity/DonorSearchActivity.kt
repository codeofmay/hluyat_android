package com.project.mt.dc.donor.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.project.mt.dc.R
import com.project.mt.dc.charity.adapter.RecyclerDonationAdapter
import com.project.mt.dc.donor.adapter.RecyclerCharityListAdapter
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DonorSearchActivity : AppCompatActivity() {
    var recycler_donorsearch: RecyclerView? = null
    var charityAdapter: RecyclerCharityListAdapter? = null
    var charityList = ArrayList<CharityModel>()
    var donationList = ArrayList<RequestModel>()
    var requestAdapter: RecyclerDonationAdapter? = null
    var swiperefresh: SwipeRefreshLayout? = null
    var tabIndicateFlag: String? = "charities"
    lateinit var lab_searchnotfound: TextView
    var fontFlag: Boolean? = null
    var findDonations:String?=null

    var lab_charities: TextView? = null
    var lab_needs: TextView? = null
    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    fun search(query: String) {

        if (tabIndicateFlag == "charities") {
            val filteredCharityList = ArrayList<CharityModel>()
            for (charity: CharityModel in charityList) {
                if (charity.charity_name!!.toLowerCase().contains(query)) {
                    filteredCharityList.add(charity)
                }
            }

            if (filteredCharityList!!.size == 0) {
                lab_searchnotfound.visibility = View.VISIBLE
                lab_searchnotfound.text = "No Charities Found"
            } else {
                lab_searchnotfound.visibility = View.GONE
            }
            charityAdapter = RecyclerCharityListAdapter(filteredCharityList, this)
            recycler_donorsearch!!.adapter = charityAdapter
            charityAdapter!!.notifyDataSetChanged()
        }
        if (tabIndicateFlag == "all") {
            val filteredDonationList = ArrayList<RequestModel>()
            for (requestModel: RequestModel in donationList!!) {
                if (requestModel.request_place!!.toLowerCase().contains(query) || requestModel.request_description!!.toLowerCase().contains(query)) {
                    filteredDonationList.add(requestModel)
                }
            }

            if (filteredDonationList!!.size == 0) {
                lab_searchnotfound.visibility = View.VISIBLE
                lab_searchnotfound.text ="No Upcoming Donation Found"
            } else {
                lab_searchnotfound.visibility = View.GONE
            }

            requestAdapter = RecyclerDonationAdapter(filteredDonationList, this, "donor")
            recycler_donorsearch!!.adapter = requestAdapter
            requestAdapter!!.notifyDataSetChanged()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_search)

        val toolbar = findViewById(R.id.toolbar_search) as Toolbar
        setSupportActionBar(toolbar)
        title = ""

        MDetect.init(this)
        fontFlag = MDetect.isUnicode()

        if(fontFlag!!){
            findDonations=getString(R.string.donor_search)
        }
        else{
            findDonations=Rabbit.uni2zg(getString(R.string.donor_search))
        }

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)

        recycler_donorsearch = findViewById(R.id.recycler_donorsearch) as RecyclerView
        swiperefresh = findViewById(R.id.swiperefresh_donorsearch) as SwipeRefreshLayout
        lab_charities = findViewById(R.id.lab_charities) as TextView
        lab_needs = findViewById(R.id.lab_needs) as TextView
        lab_searchnotfound = findViewById(R.id.lab_searchnotfound) as TextView


        val l = android.support.v7.widget.LinearLayoutManager(this)
        recycler_donorsearch!!.layoutManager = l

        val dividerItemDecoration = DividerItemDecoration(recycler_donorsearch!!.context,
                l.orientation)
        recycler_donorsearch!!.addItemDecoration(dividerItemDecoration)

        recycler_donorsearch!!.hasFixedSize()
        recycler_donorsearch!!.setItemViewCacheSize(20)
        recycler_donorsearch!!.isDrawingCacheEnabled = true
        recycler_donorsearch!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH


        lab_charities!!.setTextColor(Color.parseColor("#2196f3"))
        lab_needs!!.setTextColor(Color.parseColor("#000000"))

        swiperefresh!!.setOnRefreshListener {
            swiperefresh!!.isRefreshing = false
        }

        lab_charities!!.setOnClickListener({
            tabIndicateFlag = "charities"
            lab_charities!!.setTextColor(Color.parseColor("#2196f3"))
            lab_needs!!.setTextColor(Color.parseColor("#000000"))
            FirebaseReadService().getAllCharity()
        })

        lab_needs!!.setOnClickListener({
            tabIndicateFlag = "all"
            lab_charities!!.setTextColor(Color.parseColor("#000000"))
            lab_needs!!.setTextColor(Color.parseColor("#2196f3"))
            FirebaseReadService().getAllRequest()

        })

        val searchView = findViewById(R.id.searchview_donor) as SearchView
        searchView.isIconified = false

        searchView.queryHint = findDonations

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {

                    if (newText != null) {
                        if (fontFlag!!) {
                            search(newText.toLowerCase())
                        } else {
                            search(Rabbit.zg2uni(newText.toLowerCase()))
                        }
                    }

                }
                return true
            }

        })

        FirebaseReadService().getAllCharity()

    }

    @Subscribe
    fun getAllCharity(listEvent: Event.ListEvent<CharityModel>) {
        lab_searchnotfound.visibility = View.GONE

        if (listEvent.type == "charitylist") {
            charityList!!.clear()
            charityList = listEvent.list!!

            if (charityList!!.size == 0) {
                lab_searchnotfound.visibility = View.VISIBLE
                lab_searchnotfound.text = "No Charities Found"
            } else {
                lab_searchnotfound.visibility = View.GONE
            }
            charityAdapter = RecyclerCharityListAdapter(charityList!!, this)
            recycler_donorsearch!!.adapter = charityAdapter
            charityAdapter!!.notifyDataSetChanged()
        }
    }


    @Subscribe
    fun getCharityRequest(listEvent: Event.ListEvent<RequestModel>) {
        if (listEvent.type == "requestlist") {
            donationList!!.clear()
            donationList = listEvent.list!!
            if (donationList!!.size == 0) {
                lab_searchnotfound.visibility = View.VISIBLE
                lab_searchnotfound.text = getString(R.string.no_upcoming_donation) + " Charities"
            } else {
                lab_searchnotfound.visibility = View.GONE
            }
            requestAdapter = RecyclerDonationAdapter(donationList!!, this, "donor")
            recycler_donorsearch!!.adapter = requestAdapter
            requestAdapter!!.notifyDataSetChanged()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {

            if (item.itemId == android.R.id.home) {
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

}
