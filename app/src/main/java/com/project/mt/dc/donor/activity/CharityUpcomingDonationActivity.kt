package com.project.mt.dc.donor.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.project.mt.dc.R
import com.project.mt.dc.charity.adapter.RecyclerDonationAdapter
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CharityUpcomingDonationActivity : AppCompatActivity() {

    lateinit var recycler_charityUpcoming: RecyclerView
    lateinit var adapter: RecyclerDonationAdapter
    lateinit var lab_searchnotfound:TextView
    lateinit var charityModel:CharityModel
    var charityName:String?= null
    var fontFlag:Boolean?= null

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charity_upcoming_donation)

        MDetect.init(this)
        fontFlag=MDetect.isUnicode()

        val toolbar=findViewById(R.id.toolbar_charityupcoming)as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recycler_charityUpcoming=findViewById(R.id.recycler_charityupcoming)as RecyclerView
        lab_searchnotfound=findViewById(R.id.lab_searchnotfound)as TextView

        val l = android.support.v7.widget.LinearLayoutManager(this)
        recycler_charityUpcoming!!.layoutManager = l

        charityModel= intent.getSerializableExtra("charitymodel") as CharityModel
        if (fontFlag as Boolean){
            charityName=charityModel.charity_name
        }
        else{
            charityName=Rabbit.uni2zg(charityModel.charity_name)
        }
        title=charityName+" 's upcoming donations"
        FirebaseReadService().getCharityRequest(charityModel.charity_id!!)
    }

    @Subscribe
    fun getCharityRequest(listEvent: Event.ListEvent<RequestModel>){
        if(listEvent.type=="requestlist"){
            if(listEvent.list!!.size ==0 ){
                lab_searchnotfound.visibility= View.VISIBLE
                lab_searchnotfound.text= getString(R.string.no_upcoming_donation)+" "+charityName
            }
            else {
                lab_searchnotfound.visibility= View.GONE
            }

            adapter=RecyclerDonationAdapter(listEvent.list!!,this,"donor")
            recycler_charityUpcoming.adapter=adapter
            adapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if(item.itemId == android.R.id.home){
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
