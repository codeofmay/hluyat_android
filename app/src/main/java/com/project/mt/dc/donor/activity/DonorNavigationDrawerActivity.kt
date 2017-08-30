package com.project.mt.dc.donor.activity

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.project.mt.dc.R
import com.project.mt.dc.R.id.*
import com.project.mt.dc.AboutFragment
import com.project.mt.dc.donor.fragment.FeedFragment
import com.project.mt.dc.donor.fragment.HomeFragment
import com.project.mt.dc.event.Event
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.NotiCountUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class DonorNavigationDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var navigationView: android.support.design.widget.NavigationView?=null
    var firebaseAuth=FirebaseAuth.getInstance()
    var currentuser= firebaseAuth.currentUser!!.uid
    lateinit var feedFragment: HomeFragment
    lateinit var requireFragment: FeedFragment
    lateinit var aboutFragment: AboutFragment
    var fontFlag:Boolean?= null
    var oldNotiCount:Int?=null
    var notiDifferent:Int?= null
    var icon: LayerDrawable?= null

    //header
    var img_userimage: ImageView?= null
    var lab_username: TextView?= null
    var lab_viewprofile:TextView?=null
    var fontUtil: FontUtil?= null


    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        FirebaseReadService().getAllDonateItems("feed")
        FirebaseReadService().getDonor(currentuser)
        FirebaseReadService().getIndirectCount("notification","to_donor","to",currentuser,"donor_noti")
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_navigation_drawer)

        val toolbar = findViewById(toolbar_edit) as Toolbar
        setSupportActionBar(toolbar)
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
               this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        MDetect.init(this)
        fontFlag=MDetect.isUnicode()

        title="Hlu Yat"

        fontUtil= FontUtil(this)
        feedFragment= HomeFragment()
        requireFragment= FeedFragment()
        aboutFragment= AboutFragment()

        navigationView= findViewById(R.id.nav_view) as NavigationView
        //val swipe=findViewById(R.id.swiperefresh_donatefeed)as SwipeRefreshLayout

       // fab= findViewById(R.id.fab_donate) as FloatingActionButton

        navigationView!!.setNavigationItemSelectedListener(this)


        val view= navigationView!!.getHeaderView(0)
        img_userimage=view.findViewById(R.id.img_donorimage) as ImageView
        lab_username=view.findViewById(R.id.lab_donorname)as TextView
        lab_viewprofile=view.findViewById(R.id.lab_viewprofile)as TextView

        lab_viewprofile!!.setOnClickListener({
            val i= Intent(this,DonorProfileActivity::class.java)
            i.putExtra("caller","owner")
            startActivity(i)
        })
        img_userimage!!.setOnClickListener({
            val i= Intent(this,DonorProfileActivity::class.java)
            i.putExtra("caller","owner")
            startActivity(i)
        })

      /*  fab!!.setOnClickListener({

        })*/

       /* swipe.setOnRefreshListener {
            FirebaseReadService().getAllDonateItems("feed")
            swipe.isRefreshing=false
        }*/

        onNavigationItemSelected(navigationView!!.menu.getItem(0))
    }

    @Subscribe
    fun getDonor(donorModelEvent: Event.DonorModelEvent){
        val donorModel=donorModelEvent.donorModel
        if (donorModel != null) {
            oldNotiCount=Integer.parseInt(donorModel.noti_count)

            if(fontFlag!!) {
                lab_username!!.text = donorModel.donor_name
            }
            else{
                lab_username!!.text = Rabbit.uni2zg(donorModel.donor_name)
            }

            lab_username!!.typeface= fontUtil!!.title_font
            lab_viewprofile!!.typeface= fontUtil!!.open_san_regular
            Glide.with(this).
                    load(donorModel.donor_image)
                    .placeholder(R.drawable.ic_person_black_24dp).
                    bitmapTransform(CropCircleTransformation(this)).
                    into(img_userimage)
        }
    }

    @Subscribe
    fun getNotiCount(stringEvent: Event.StringEvent){
        if(stringEvent.type=="donor_noti"){
            val newNotiCount: Int=Integer.parseInt(stringEvent.string)
            notiDifferent=newNotiCount-oldNotiCount!!

            NotiCountUtil().setBadgeCount(this, icon!!, notiDifferent!!)
        }
    }


    override fun onBackPressed() {
        val drawer = findViewById(drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.donor_navigation_drawer, menu)
        val item = menu.findItem(R.id.action_noti)
        icon = item.icon as LayerDrawable
        NotiCountUtil().setBadgeCount(this, icon!!, 0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if(id==R.id.action_add){
            val i=Intent(this, DonateFormActivity::class.java)
            startActivity(i)
        }
        if (id == R.id.action_noti) {
            val i=Intent(this, DonorNotiActivity::class.java)
            i.putExtra("caller","home")
            startActivity(i)
            return true
        }
        if (id == R.id.action_search) {
            val i=Intent(this, DonorSearchActivity::class.java)
            startActivity(i)
            return true
        }


        return super.onOptionsItemSelected(item)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == nav_home) {
            title="Your Items"
            supportFragmentManager.beginTransaction().replace(R.id.donor_container,feedFragment).commit()
        }
        else if (id == nav_feed) {
            title="Feed"
            supportFragmentManager.beginTransaction().replace(R.id.donor_container,requireFragment).commit()
        } else if (id == nav_logOut) {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            Toast.makeText(this,"Logged Out", android.widget.Toast.LENGTH_LONG).show()
            val i= android.content.Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
        if(id== nav_aboutUs){
            supportFragmentManager.beginTransaction().replace(R.id.donor_container,aboutFragment).commit()
        }

        val drawer = findViewById(drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}
