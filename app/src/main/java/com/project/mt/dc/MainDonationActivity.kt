package com.project.mt.dc

import android.content.DialogInterface
import android.content.Intent
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
import com.project.mt.dc.donor.fragment.BottonSheetFragment
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.util.FontUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MainDonationActivity : AppCompatActivity() {

    lateinit var img_donationImage: ImageView
    lateinit var img_charityImage: ImageView
    lateinit var lab_donationName: TextView
    lateinit var lab_donationLocation: TextView
    lateinit var lab_donationDate: TextView
    lateinit var lab_donationDescription: TextView
    lateinit var lab_charityName: TextView
    lateinit var lab_nodonor:TextView
    lateinit var recycler_mainDonation: RecyclerView
    lateinit var donationModel: RequestModel
    lateinit var caller: String
    var fontUtil:FontUtil?= null
    lateinit var bottomSheetDialogFragment: BottonSheetFragment
    var fontFlag:Boolean?=null


    val firebaseReadService=FirebaseReadService()
    val firebasWriteService=FirebaseWriteService()

    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    var charity_phone: String? = null

    var adapter: RecyclerMainDonationAdapter? = null

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_donation)
        MDetect.init(this)
        fontFlag=MDetect.isUnicode()

        fontUtil= FontUtil(this)

        val toolbar = findViewById(R.id.toolbar_mainDonation) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        img_donationImage = findViewById(R.id.img_donationImage) as ImageView
        img_charityImage = findViewById(R.id.img_charityimage) as ImageView
        lab_charityName = findViewById(R.id.lab_charityname) as TextView
        lab_donationName = findViewById(R.id.lab_donationname) as TextView
        lab_donationLocation=findViewById(R.id.lab_donationplace)as TextView
        lab_nodonor=findViewById(R.id.lab_nodonor)as TextView
        lab_donationDate = findViewById(R.id.lab_donationdate) as TextView
        lab_donationDescription = findViewById(R.id.lab_donationdescription) as TextView
        recycler_mainDonation = findViewById(R.id.recycler_maindonation) as RecyclerView
        recycler_mainDonation.layoutManager = LinearLayoutManager(this)

        donationModel = intent.getSerializableExtra("donationmodel") as RequestModel
        caller = intent.getStringExtra("caller")

        Glide.with(this)
                .load(donationModel.request_image)
                .into(img_donationImage)
        if(fontFlag!!) {
            lab_donationName.text = donationModel.request_place
            lab_donationName.text = donationModel.request_place
            lab_donationDescription.text = donationModel.request_description
            lab_donationLocation.text = donationModel.request_location
        }
        else{
            lab_donationName.text = Rabbit.uni2zg(donationModel.request_place)
            lab_donationDescription.text = Rabbit.uni2zg(donationModel.request_description)
            lab_donationLocation.text = Rabbit.uni2zg(donationModel.request_location)
        }

        lab_charityName.typeface= fontUtil!!.title_font
        lab_donationName.typeface= fontUtil!!.title_font
        lab_donationDescription.typeface=fontUtil!!.regular_font

        lab_donationDate.text = donationModel.request_date
        firebaseReadService.getCharity(donationModel.charity_id!!)
        firebaseReadService.getCharityDonorList(donationModel.charity_id!!, donationModel.request_id!!,"charitydonorlist")
    }

    @Subscribe
    fun getCharity(charityModelEvent: Event.CharityModelEvent) {

        val charityModel = charityModelEvent.charityModel
        if (charityModel != null) {
            Glide.with(this)
                    .load(charityModel.charity_image)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .bitmapTransform(CropCircleTransformation(this))
                    .into(img_charityImage)
            if(fontFlag!!) {
                lab_charityName.text = charityModel.charity_name
            }
            else{
                lab_charityName.text = Rabbit.uni2zg(charityModel.charity_name)
            }

            charity_phone = charityModel.charity_phone

            title = lab_charityName.text
        }
    }

    @Subscribe
    fun getChosenItem(modelEvent: Event.ModelEvent<ItemInfoModel>) {
        val itemModel = modelEvent.model
        val confirmDialog=android.support.v7.app.AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle("Confirmation")
                .setIcon(android.R.drawable.ic_dialog_info)
        var message:String
        if (fontFlag!!){
            message= "Are you sure to donate " + itemModel!!.item_category + " : " + itemModel!!.item_amount
        }
        else{
            message= "Are you sure to donate " + Rabbit.uni2zg(itemModel!!.item_category) + " : " + Rabbit.uni2zg(itemModel!!.item_amount)
        }
        confirmDialog.setMessage(message)
        confirmDialog.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, position: Int) {
                val notiModel= NotificationModel()
                notiModel.item_id=itemModel.item_key
                notiModel.item_category=itemModel.item_category
                notiModel.item_amount=itemModel.item_amount
                notiModel.donor_id=currentUser
                notiModel.charity_id=donationModel.charity_id
                notiModel.request_id=donationModel.request_id
                firebasWriteService.setDonorList(donationModel.charity_id!!,notiModel)
            }

        })
        bottomSheetDialogFragment.dismiss()
        confirmDialog.show()
    }

    @Subscribe
    fun getDonorList(listEvent: Event.ListEvent<NotificationModel>) {
        if (listEvent.type == "charitydonorlist") {

            if (listEvent.list!!.size==0){
                lab_nodonor.visibility=View.VISIBLE
            }
            else{
                lab_nodonor.visibility=View.GONE
            }
            adapter = RecyclerMainDonationAdapter(listEvent.list!!, donationModel, caller, this)
            recycler_mainDonation.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }
    }

    @Subscribe
    fun getDonorItems(listEvent: Event.ListEvent<ItemInfoModel>) {
        if (listEvent.type == "donoritemgrid") {

            showButtomsheet(listEvent.list!!)
        }
    }



    fun showButtomsheet(list: ArrayList<ItemInfoModel>) {

        bottomSheetDialogFragment = BottonSheetFragment(this, null, list!!, "donor")
        bottomSheetDialogFragment.show(this.supportFragmentManager, bottomSheetDialogFragment!!.tag)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (caller == "donor") {
            menuInflater.inflate(R.menu.menu_donormaindonation, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == android.R.id.home) {
                finish()
            }
            if (item.itemId == R.id.action_donoradd) {
                firebaseReadService.getDonateItemByDonorGrid(currentUser)
            }
            if (item.itemId == R.id.action_donorcall) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + charity_phone)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}


