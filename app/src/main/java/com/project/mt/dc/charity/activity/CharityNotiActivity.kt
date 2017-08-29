package com.project.mt.dc.charity.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.project.mt.dc.R
import com.project.mt.dc.charity.adapter.RecyclerNotificationAdapter
import com.project.mt.dc.donor.activity.LoginActivity
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.MethodUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CharityNotiActivity : AppCompatActivity() {

    var adapter: RecyclerNotificationAdapter? = null
    var notiList = ArrayList<NotificationModel>()
    var recycler_notiList: RecyclerView? = null
    var swiperefresh: SwipeRefreshLayout?= null
    val firebaseAuth= FirebaseAuth.getInstance()
    var currentUser:String?=null
    val firebaseDB=FirebaseDatabase.getInstance()
    lateinit var btn_login:TextView
    lateinit var linear_notiCall: LinearLayout
    lateinit var caller:String

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
        setContentView(R.layout.activity_charity_noti)

        recycler_notiList = findViewById(R.id.recycler_charity_noti) as RecyclerView
        swiperefresh=findViewById(R.id.swiperefresh_charitynoti) as SwipeRefreshLayout

        caller=intent.getStringExtra("caller")
        linear_notiCall=findViewById(R.id.linear_notiCall)as LinearLayout
        btn_login=findViewById(R.id.btn_login)as TextView

        val toolbar=findViewById(R.id.toolbar_charitynoti)as Toolbar
        setSupportActionBar(toolbar)
        title="Notification"

        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)

        val layoutManager: LinearLayoutManager = LinearLayoutManager(this)
        recycler_notiList!!.layoutManager = layoutManager as RecyclerView.LayoutManager

        val dividerItemDecoration = DividerItemDecoration(recycler_notiList!!.context,
                layoutManager.orientation)
        recycler_notiList!!.addItemDecoration(dividerItemDecoration)


        swiperefresh!!.setOnRefreshListener {
            FirebaseReadService().getNotificationById("to_charity", currentUser!!)
            swiperefresh!!.isRefreshing= false
        }

        btn_login.setOnClickListener({
            val i=Intent(this,LoginActivity::class.java)
            startActivity(i)
            this.finish()
        })

        try {
            currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseReadService().getNotificationById("to_charity", currentUser!!)
        } catch (e: KotlinNullPointerException) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    @Subscribe
    fun getCharityNoti(notiEvent: Event.CharityNotiEvent) {
        val notiList= MethodUtil().reverse(notiEvent.list!!)

        if(caller == "noti"){
            if (notiEvent.list!!.size ==0){
                linear_notiCall.visibility=View.VISIBLE
            }
        }
        else{
            linear_notiCall.visibility=View.GONE
        }

        firebaseDB.getReference("charity").child(currentUser).child("noti_count").setValue(notiList.size.toString())
        adapter = RecyclerNotificationAdapter(notiList,this)
        recycler_notiList!!.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==android.R.id.home){
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
