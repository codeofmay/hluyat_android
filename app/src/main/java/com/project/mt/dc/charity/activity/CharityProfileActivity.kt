package com.project.mt.dc.charity.activity


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.mt.dc.R
import com.project.mt.dc.charity.adapter.RecyclerDonatedListAdapter
import com.project.mt.dc.donor.activity.EditProfileActivity
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CharityProfileActivity : AppCompatActivity() {

    var img_charityImage: ImageView? = null
    var lab_charityName: TextView? = null
    var lab_pendingCount: TextView? = null
    var lab_donatedCount: TextView? = null
    lateinit var lab_charity_nodonated:TextView
    lateinit var lab_charityPhone:TextView
    lateinit var lab_charityYear:TextView
    lateinit var lab_charityLocation:TextView
    lateinit var lab_charityDescription:TextView
    var recycler_donatedList: RecyclerView? = null
    val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var currentUser: String
    lateinit var caller: String
    var donatedAdapter: RecyclerDonatedListAdapter? = null
    var fontFlag:Boolean?= null

    var fontUtil: FontUtil? = null
    var lab_profiletext1: TextView? = null
    var lab_profiletext2: TextView? = null
    var charityModel=CharityModel()


    fun onClickDonated(v: View) {

        lab_donatedCount!!.setTextColor(Color.parseColor("#2196f3"))
        lab_profiletext1!!.setTextColor(Color.parseColor("#2196f3"))

        recycler_donatedList!!.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        FirebaseReadService().getCharityDonated(currentUser)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        FirebaseReadService().getCharity(currentUser)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charity_profile)

        MDetect.init(this)
        fontFlag=MDetect.isUnicode()

        caller = intent.getStringExtra("caller")

        val toolbar = findViewById(R.id.toolbar_charityprofile) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        img_charityImage = findViewById(R.id.img_charityimage) as ImageView
        lab_charityName = findViewById(R.id.lab_charityname) as TextView
        lab_charity_nodonated = findViewById(R.id.lab_charity_nodonated) as TextView
        lab_donatedCount = findViewById(R.id.lab_donatedcount) as TextView
        lab_charityLocation = findViewById(R.id.lab_charitylocation) as TextView
        lab_charityDescription = findViewById(R.id.lab_charitydescription) as TextView

        lab_profiletext1 = findViewById(R.id.profileText1) as TextView
        lab_charityPhone = findViewById(R.id.lab_charityphone) as TextView
        lab_charityYear = findViewById(R.id.lab_charityyear) as TextView
        recycler_donatedList = findViewById(R.id.recycler_charitydonatedList) as RecyclerView

        recycler_donatedList!!.hasFixedSize()
        recycler_donatedList!!.isNestedScrollingEnabled = true
        recycler_donatedList!!.scrollToPosition(0)

        fontUtil = FontUtil(this)
        lab_charityName!!.typeface = fontUtil!!.title_font
        lab_donatedCount!!.typeface = fontUtil!!.medium_font
        lab_profiletext1!!.typeface = fontUtil!!.regular_font


        val layoutManager: LinearLayoutManager = LinearLayoutManager(this)
        recycler_donatedList!!.layoutManager = layoutManager

        lab_donatedCount!!.setTextColor(Color.parseColor("#2196f3"))
        lab_profiletext1!!.setTextColor(Color.parseColor("#2196f3"))

        if (caller == "visitor") {
            currentUser = intent.getStringExtra("id")
        }
        if (caller == "owner") {
            currentUser = firebaseAuth.currentUser!!.uid
        }

        lab_charityPhone.setOnClickListener({
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + lab_charityPhone.text.toString())
            startActivity(intent)
        })
        //FirebaseReadService().getCount("charity", currentUser, "donated","charity_donated")
        FirebaseReadService().getCharityDonated(currentUser)

    }

    /* @Subscribe
     fun getTodonateList(itemListEvent: UserItemListEvent){

         toDonateAdapter = RecyclerOwnItemsAdapter(itemListEvent.list!!, this)
         recycler_donatedList!!.adapter = toDonateAdapter
         toDonateAdapter!!.notifyDataSetChanged()

     }
 */
    @Subscribe
    fun getDonatedList(listEvent: Event.ListEvent<RequestModel>) {
        if(listEvent.type=="charity_donated_list") {
            val donatedList = listEvent.list
            if (donatedList != null) {
                if (donatedList.size==0){
                    lab_charity_nodonated.visibility=View.VISIBLE
                }
                else{
                    lab_charity_nodonated.visibility=View.GONE
                }
            }

            lab_donatedCount!!.text = listEvent.list!!.size.toString()
            donatedAdapter = RecyclerDonatedListAdapter(donatedList!!,currentUser, this)
            recycler_donatedList!!.adapter = donatedAdapter
            donatedAdapter!!.notifyDataSetChanged()
        }

    }

    @Subscribe
    fun getCharity(charityModelEvent: Event.CharityModelEvent) {
        charityModel = charityModelEvent.charityModel!!
        if (charityModel != null) {
            Glide.with(this).
                    load(charityModel.charity_image).
                    placeholder(R.drawable.ic_person_black_24dp).
                    bitmapTransform(CropCircleTransformation(applicationContext)).
                    into(img_charityImage)
            lab_charityPhone.text=charityModel.charity_phone
            lab_charityYear.text=charityModel.charity_year
            if (charityModel.charity_description.isNullOrEmpty()) {
               lab_charityDescription.visibility=View.GONE
            }

            var charityName:String
            if (fontFlag!!) {
                charityName= charityModel.charity_name!!
                lab_charityDescription.text=charityModel.charity_description
                lab_charityLocation.text=charityModel.charity_city
            }
            else{
                charityName = Rabbit.uni2zg(charityModel.charity_name)
                lab_charityDescription.text=Rabbit.uni2zg(charityModel.charity_description)
                lab_charityLocation.text=Rabbit.uni2zg(charityModel.charity_city)
            }
            lab_charityName!!.text = charityName
            if (caller == "visitor") {
                title = charityName
            } else {
                title = "Profile"
            }

        }
    }

    @Subscribe
    fun getDonatedCount(stringEvent: Event.StringEvent) {

        if (stringEvent.type == "charity_donated") {
            lab_donatedCount!!.text = stringEvent.string
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (caller == "owner") {
            menuInflater.inflate(R.menu.menu_profile, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            this.finish()
        }
        if (item.itemId == R.id.action_editprofile) {
            val i = Intent(this, com.project.mt.dc.charity.activity.EditProfileActivity::class.java)
            i.putExtra("charitymodel", charityModel)
            startActivity(i)
        }
        return super.onOptionsItemSelected(item)

    }


}

