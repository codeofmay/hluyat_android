package com.project.mt.dc.donor.activity

import android.content.Intent
import android.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
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
import com.project.mt.dc.R.id.btn_login
import com.project.mt.dc.R.id.linear_notiCall
import com.project.mt.dc.donor.adapter.RecyclerNotificationAdapter
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.MethodUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DonorNotiActivity : AppCompatActivity() {

    var adapter: RecyclerNotificationAdapter? = null
    var notiList = ArrayList<NotificationModel>()
    var recycler_notiList: RecyclerView? = null
    var user_id: String? = null
    val firebaseDB = FirebaseDatabase.getInstance()
    lateinit var linear_notiCall: LinearLayout
    lateinit var caller: String
    lateinit var btn_login: TextView

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        try {
            user_id = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseReadService().getNotificationById("to_donor", user_id!!)
        } catch (e: KotlinNullPointerException) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_noti)

        val toolbar = findViewById(R.id.toolbar_donornoti) as Toolbar
        setSupportActionBar(toolbar)
        title = "Notification"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)

        recycler_notiList = findViewById(R.id.recycler_donor_noti) as RecyclerView
        linear_notiCall = findViewById(R.id.linear_notiCall) as LinearLayout
        val swiperefresh_donornoti = findViewById(R.id.swiperefresh_donornoti) as SwipeRefreshLayout
        btn_login = findViewById(R.id.btn_login) as TextView

        caller = intent.getStringExtra("caller")

        val layoutManager: LinearLayoutManager = LinearLayoutManager(this)
        recycler_notiList!!.layoutManager = layoutManager

        swiperefresh_donornoti.setOnRefreshListener {
            FirebaseReadService().getNotificationById("to_donor", user_id!!)
            swiperefresh_donornoti.isRefreshing = false
        }
        btn_login.setOnClickListener({
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            this.finish()
        })

        val dividerItemDecoration = DividerItemDecoration(recycler_notiList!!.context,
                layoutManager.orientation)
        recycler_notiList!!.addItemDecoration(dividerItemDecoration)
        recycler_notiList!!.hasFixedSize()
        recycler_notiList!!.setItemViewCacheSize(20)
        recycler_notiList!!.isDrawingCacheEnabled = true
        recycler_notiList!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH


    }


    @Subscribe
    fun getDonorNoti(donorNotiEvent: Event.DonorNotiEvent) {

        if (caller == "noti") {
            if (donorNotiEvent.list!!.size == 0) {
                linear_notiCall.visibility = View.VISIBLE
            }
        } else {
            linear_notiCall.visibility = View.GONE
        }


        firebaseDB.getReference("donor").child(user_id).child("noti_count").setValue(donorNotiEvent.list!!.size.toString())
        val requestNotiList = MethodUtil().reverse(donorNotiEvent.list!!)
        adapter = RecyclerNotificationAdapter(requestNotiList, this)
        recycler_notiList!!.adapter = adapter
        adapter!!.notifyDataSetChanged()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)

    }
}
