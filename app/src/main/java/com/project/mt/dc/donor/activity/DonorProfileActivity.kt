package com.project.mt.dc.donor.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.mt.dc.MainDonationDialogFragment
import com.project.mt.dc.R
import com.project.mt.dc.donor.adapter.RecyclerDonatedListAdapter
import com.project.mt.dc.donor.adapter.RecyclerOwnItemsAdapter
import com.project.mt.dc.donor.fragment.BioDialogFragment
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class DonorProfileActivity : AppCompatActivity() {

    var img_donorImage: ImageView? = null
    var lab_donorName: TextView? = null
    var lab_pendingCount: TextView? = null
    var lab_donatedCount: TextView? = null
    var recycler_donatedList: RecyclerView? = null
    val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var currentUser: String
    lateinit var id: String
    lateinit var caller: String
    var fontFlag: Boolean? = null

    lateinit var donorModel: DonorInfoModel
    var donatedAdapter: RecyclerDonatedListAdapter? = null
    var toDonateAdapter: RecyclerOwnItemsAdapter? = null
    var fontUtil: FontUtil? = null
    var linear_toDonate: LinearLayout? = null
    var linear_donated: LinearLayout? = null
    var linear_profileOption: LinearLayout? = null
    var lab_profiletext1: TextView? = null
    var lab_profiletext2: TextView? = null
    lateinit var lab_donorLocation: TextView
    lateinit var lab_donorPhone: TextView
    lateinit var lab_nodonating: TextView
    lateinit var lab_donorbio: TextView
    lateinit var lab_nodonated: TextView

    fun onClickToDonate(v: View) {

        lab_donatedCount!!.setTextColor(Color.parseColor("#5c6773"))
        lab_profiletext1!!.setTextColor(Color.parseColor("#a1aab3"))


        lab_pendingCount!!.setTextColor(Color.parseColor("#2196f3"))
        lab_profiletext2!!.setTextColor(Color.parseColor("#2196f3"))


        recycler_donatedList!!.layoutManager = LinearLayoutManager(this)
        FirebaseReadService().getDonateItemByDonor(currentUser)
    }

    fun onClickDonated(v: View) {


        lab_pendingCount!!.setTextColor(Color.parseColor("#5c6773"))
        lab_profiletext2!!.setTextColor(Color.parseColor("#a1aab3"))


        lab_donatedCount!!.setTextColor(Color.parseColor("#2196f3"))
        lab_profiletext1!!.setTextColor(Color.parseColor("#2196f3"))

        recycler_donatedList!!.layoutManager = LinearLayoutManager(this)
        FirebaseReadService().getDonorDonated(currentUser)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)

        lab_donatedCount!!.setTextColor(Color.parseColor("#2196f3"))
        lab_profiletext1!!.setTextColor(Color.parseColor("#2196f3"))

        lab_pendingCount!!.setTextColor(Color.parseColor("#5c6773"))
        lab_profiletext2!!.setTextColor(Color.parseColor("#a1aab3"))

        FirebaseReadService().getDonor(currentUser)
        FirebaseReadService().getCount("donor", currentUser, "donated_item", "donor_donated")
        FirebaseReadService().getIndirectCount("donateitems", "donor_id", currentUser)
        FirebaseReadService().getDonorDonated(currentUser)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_profile)

        MDetect.init(this)
        fontFlag = MDetect.isUnicode()

        caller = intent.getStringExtra("caller")

        val toolbar = findViewById(R.id.toolbar_donorprofile) as Toolbar
        setSupportActionBar(toolbar)
        title = "Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        img_donorImage = findViewById(R.id.img_donorimage) as ImageView
        lab_donorName = findViewById(R.id.lab_donorname) as TextView
        lab_pendingCount = findViewById(R.id.lab_pendingcount) as TextView
        lab_donatedCount = findViewById(R.id.lab_donatedcount) as TextView
        lab_donorbio = findViewById(R.id.lab_donorbio) as TextView
        /* lab_donorLocation = findViewById(R.id.lab_donorlocation) as TextView
         lab_donorPhone=findViewById(R.id.lab_donorphone) as TextView*/
        linear_donated = findViewById(R.id.linear_donor_donated) as LinearLayout
        linear_toDonate = findViewById(R.id.linear_donor_todonate) as LinearLayout
        linear_profileOption = findViewById(R.id.linear_donorprofile_option) as LinearLayout

        lab_nodonated = findViewById(R.id.lab_donor_nodonated) as TextView
        lab_nodonating = findViewById(R.id.lab_donor_nodonating) as TextView

        lab_profiletext1 = findViewById(R.id.profileText1) as TextView
        lab_profiletext2 = findViewById(R.id.profileText2) as TextView
        recycler_donatedList = findViewById(R.id.recycler_donordonatedList) as RecyclerView

        recycler_donatedList!!.hasFixedSize()
        recycler_donatedList!!.isNestedScrollingEnabled = false
        recycler_donatedList!!.smoothScrollToPosition(0)

        fontUtil = FontUtil(this)
        lab_donorName!!.typeface = fontUtil!!.title_font
        lab_donatedCount!!.typeface = fontUtil!!.medium_font
        lab_pendingCount!!.typeface = fontUtil!!.medium_font
        lab_profiletext1!!.typeface = fontUtil!!.regular_font
        lab_profiletext2!!.typeface = fontUtil!!.regular_font

        if (fontFlag!!) {
            lab_profiletext2!!.text = getString(R.string.todonate)
            lab_profiletext1!!.text = getString(R.string.donated)
        } else {
            lab_profiletext2!!.text = Rabbit.uni2zg(getString(R.string.todonate))
            lab_profiletext1!!.text = Rabbit.uni2zg(getString(R.string.donated))
        }

        val layoutManager: LinearLayoutManager = LinearLayoutManager(this)
        recycler_donatedList!!.layoutManager = layoutManager

        if (caller == "visitor") {
            currentUser = intent.getStringExtra("id")
        }
        if (caller == "owner") {
            currentUser = firebaseAuth.currentUser!!.uid
        }

        lab_donorbio.setOnClickListener({
            if (caller == "owner" && lab_donorbio.text=="Enter Bio") {
                val dialogFragment = BioDialogFragment(currentUser)
                dialogFragment.show(this.supportFragmentManager, "Dialog Fragment")
            }
        })


    }

    @Subscribe
    fun getTodonateList(listEvent: Event.ListEvent<ItemInfoModel>) {
        if (listEvent.type == "donoritemlist") {
            if (listEvent.list!!.size == 0) {
                lab_nodonating.visibility = View.VISIBLE
                lab_nodonated.visibility = View.GONE
            } else {
                lab_nodonating.visibility = View.GONE
                lab_nodonated.visibility = View.GONE
            }
            toDonateAdapter = RecyclerOwnItemsAdapter(listEvent.list!!, currentUser, caller, this)
            recycler_donatedList!!.adapter = toDonateAdapter
            toDonateAdapter!!.notifyDataSetChanged()
        }

    }

    @Subscribe
    fun getDonatedList(profileListEvent: Event.ProfileListEvent) {

        val donatedList = profileListEvent.list

        if (donatedList != null) {
            if (donatedList.size == 0) {
                lab_nodonating.visibility = View.GONE
                lab_nodonated.visibility = View.VISIBLE
            } else {
                lab_nodonating.visibility = View.GONE
                lab_nodonated.visibility = View.GONE
            }
        }
        donatedAdapter = RecyclerDonatedListAdapter(donatedList!!, this)
        recycler_donatedList!!.adapter = donatedAdapter
        donatedAdapter!!.notifyDataSetChanged()


    }

    @Subscribe
    fun getDonorInfo(donorModelEvent: Event.DonorModelEvent) {
        donorModel = donorModelEvent.donorModel!!
        if (donorModel != null) {
            var bio: String? = null
            Glide.with(this).
                    load(donorModel.donor_image)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .bitmapTransform(CropCircleTransformation(applicationContext)).
                    into(img_donorImage)
            if (donorModel.donor_bio.isNullOrEmpty() && caller=="owner") {
                bio = "Enter Bio"
            }
            else if(donorModel.donor_bio.isNullOrEmpty() && caller=="visitor") {
                lab_donorbio.visibility=View.GONE
            }
            else {
                bio = donorModel.donor_bio
            }

            if (fontFlag!!) {
                lab_donorName!!.text = donorModel.donor_name
                lab_donorbio.text = bio
                /*  lab_donorPhone!!.text=donorModel.donor_phone
                  lab_donorLocation.text=donorModel.donor_city+" . "+donorModel.donor_township*/
            } else {
                lab_donorName!!.text = Rabbit.uni2zg(donorModel.donor_name)
                lab_donorbio.text = Rabbit.uni2zg(bio)
                /*  lab_donorPhone!!.text=donorModel.donor_phone
                  val city=Rabbit.uni2zg(donorModel.donor_city)
                  val township=Rabbit.uni2zg(donorModel.donor_township)
                  lab_donorLocation.text=city+" . "+township*/
            }
            if (caller == "visitor") {
                if (fontFlag!!) {
                    title = donorModel.donor_name
                } else {
                    title = Rabbit.uni2zg(donorModel.donor_name)
                }
                title = donorModel.donor_name
            } else {
                title == "Profile"
            }

        }
    }

    @Subscribe
    fun getDonatedCount(stringEvent: Event.StringEvent) {
        if (stringEvent.type == "pending") {
            lab_pendingCount!!.text = stringEvent.string
        }

        if (stringEvent.type == "donor_donated") {
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
            val i = Intent(this, EditProfileActivity::class.java)
            i.putExtra("donor", donorModel)
            startActivity(i)
        }
        return super.onOptionsItemSelected(item)

    }


}
