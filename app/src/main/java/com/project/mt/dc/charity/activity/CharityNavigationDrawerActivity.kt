package com.project.mt.dc.charity.activity

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
import com.google.firebase.auth.FirebaseAuth
import com.project.mt.dc.R
import com.project.mt.dc.R.id.*
import com.project.mt.dc.R.layout.activity_charity_navigation_drawer
import com.project.mt.dc.charity.fragment.HomeFragment
import com.project.mt.dc.donor.activity.LoginActivity
import com.project.mt.dc.AboutFragment
import com.project.mt.dc.event.Event
import com.project.mt.dc.event.Event.StringEvent
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.NotiCountUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CharityNavigationDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var currentUser= FirebaseAuth.getInstance().currentUser!!.uid

    //header
    lateinit var lab_charityName:TextView
    lateinit var lab_viewProfile:TextView
    lateinit var img_charityImage: ImageView
    lateinit var fontUtil:FontUtil
    lateinit var homeFragment:HomeFragment
    lateinit var aboutFragment: AboutFragment
    var oldNotiCount:Int?=null
    var notiDifferent:Int?= null
    var icon:LayerDrawable?= null
    var fontFlag:Boolean?=null




    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        FirebaseReadService().getCharity(currentUser)
        FirebaseReadService().getIndirectCount("notification","to_charity","to",currentUser,"charity_noti")

    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }



    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_charity_navigation_drawer)
        val toolbar = findViewById(toolbar_edit) as Toolbar
        setSupportActionBar(toolbar)
        title="Hlu Yat"
        fontUtil= FontUtil(this)

        MDetect.init(this)
        fontFlag=MDetect.isUnicode()

        homeFragment= HomeFragment()
        aboutFragment= AboutFragment()

        val drawer = findViewById(drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        var headerView=navigationView.getHeaderView(0)
        img_charityImage=headerView.findViewById(R.id.img_charityimage)as ImageView
        lab_charityName=headerView.findViewById(R.id.lab_charityname)as TextView
        lab_viewProfile=headerView.findViewById(R.id.lab_viewprofile)as TextView

        lab_charityName.typeface=fontUtil.title_font
        lab_viewProfile.typeface=fontUtil.open_san_regular

        lab_viewProfile.setOnClickListener({
            val i= Intent(this,CharityProfileActivity::class.java)
            i.putExtra("caller","owner")
            startActivity(i)
        })

        img_charityImage.setOnClickListener({
            val i= Intent(this,CharityProfileActivity::class.java)
            i.putExtra("caller","owner")
            startActivity(i)
        })
        navigationView.menu.getItem(0).isChecked = true
        val onNavigationItemSelected = onNavigationItemSelected(navigationView.menu.getItem(0))

    }

    @Subscribe
    fun getCharity(charityModelEvent: Event.CharityModelEvent){
        var charityModel= charityModelEvent.charityModel
        if (charityModel != null) {
            Glide.with(this)
                    .load(charityModel.charity_image)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .bitmapTransform(CropCircleTransformation(this))
                    .into(img_charityImage)

            oldNotiCount=Integer.parseInt(charityModel.noti_count)

            if (fontFlag!!) {
                lab_charityName.text = charityModel.charity_name
            }
            else{
                lab_charityName.text = Rabbit.uni2zg(charityModel.charity_name)
            }


        }

    }

    @Subscribe
    fun getNotiCount(stringEvent: StringEvent){
        if(stringEvent.type=="charity_noti"){
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
        menuInflater.inflate(R.menu.charity_navigation_drawer, menu)
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

        if (id== action_search){
            val i = Intent(this, CharitySearchActivity::class.java)
            startActivity(i)
        }
        if (id == action_noti) {
            val i= Intent(this,CharityNotiActivity::class.java)
            i.putExtra("caller","home")
            startActivity(i)
            return true
        }
        if(id== action_adddonation){
            val i= Intent(this,RequestFormActivity::class.java)
            i.putExtra("caller","new")
            startActivity(i)
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == nav_charityhome) {
            supportFragmentManager.beginTransaction().replace(R.id.charity_container,homeFragment).commit()
        } else if (id == nav_aboutUs) {
            supportFragmentManager.beginTransaction().replace(R.id.charity_container,aboutFragment).commit()

        } else if (id == nav_logout) {

            FirebaseAuth.getInstance().signOut()
           Toast.makeText(this,"Logged out",Toast.LENGTH_SHORT).show()
            val i= Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }

        val drawer = findViewById(drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
