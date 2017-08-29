package com.project.mt.dc.donor.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.preference.PreferenceManager
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.project.mt.dc.R
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.service.ShowAddressDialogService
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


class ProfileSetupActivity : android.support.v7.app.AppCompatActivity() {
    var chosenCity: String? = null
    var chosenTownship: String? = null
    var donorName: android.widget.TextView? = null
    var donorPhone: android.widget.TextView? = null
    var donorImage: android.widget.ImageView? = null

    var donorAddr: TextView? = null
    var donorEmail: String? = null
    var donorImageUrl: String? = null
    var currentUser=FirebaseAuth.getInstance().currentUser
    var donorId: String? = null
    var fontFlag:Boolean?= null
    val donorReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("donor")
    var ok:LinearLayout?= null
    var lab_ok:TextView?= null
    var address:String?=null
    var progressDialog:ProgressDialog?= null



    override fun onResume() {
        super.onResume()
        org.greenrobot.eventbus.EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        MDetect.init(this)
        fontFlag=MDetect.isUnicode()

        setContentView(com.project.mt.dc.R.layout.activity_donor_profile_setup)
        var showAddressDialogService=ShowAddressDialogService("profile_setup",this)

        progressDialog= ProgressDialog(this)
        progressDialog!!.setMessage("Preparing Account")

        showAddressDialogService.setData()
        if(supportActionBar!= null){
            supportActionBar!!.title="Setup Profile"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        donorName = findViewById(com.project.mt.dc.R.id.text_donorName) as android.widget.TextView
        lab_ok = findViewById(com.project.mt.dc.R.id.lab_donorRegister) as android.widget.TextView
        donorPhone = findViewById(com.project.mt.dc.R.id.text_donorPhone) as android.widget.TextView
        donorImage = findViewById(com.project.mt.dc.R.id.img_donor) as android.widget.ImageView
        donorAddr = findViewById(com.project.mt.dc.R.id.btn_donorAddr) as android.widget.TextView
        ok=findViewById(R.id.button_donorRegister)as LinearLayout
        val img_arrowdown=findViewById(R.id.img_arrowdown)as ImageView



        if (fontFlag!!){
            lab_ok!!.text=getString(R.string.ok)
            donorAddr!!.text=getString(R.string.choose_address)

        }
        else{
            lab_ok!!.text=Rabbit.uni2zg(getString(R.string.ok))
            donorAddr!!.text=Rabbit.uni2zg(getString(R.string.choose_address))

        }
        val donorBundle = intent.getBundleExtra("donorbundle")

        donorEmail = donorBundle.getString("donorEmail")
        donorName!!.text = donorBundle.getString("donorName")
        donorImageUrl = donorBundle.getString("donorImage")
        donorId = donorBundle.getString("donorId")

        val imgUri = android.net.Uri.parse(donorImageUrl)
        Glide.with(applicationContext)
                .load(imgUri)
                .placeholder(R.drawable.ic_person_black_24dp)
                .bitmapTransform(CropCircleTransformation(this))
                .into(donorImage)

        donorAddr!!.setOnClickListener {
            showAddressDialogService.setCityDialog()
        }

        img_arrowdown.setOnClickListener {
            showAddressDialogService.setCityDialog()
        }

        ok!!.setOnClickListener({
          onClickRegister()
        })
    }


    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    fun onAddressEvent(event: Event.AddressEvent) {
        chosenCity = event.city
        chosenTownship = event.township
        donorAddr!!.text = chosenTownship

    }

    fun isValidPhone(phone:String):Boolean{

        if(phone.length>6) {

            val phUtil = PhoneNumberUtil.getInstance()
            val parseNumber = phUtil.parse(phone, "MM")

            if(phUtil.isValidNumber(parseNumber)){
                donorPhone!!.text = "+${parseNumber.countryCode} ${parseNumber.nationalNumber}"

                return true

            }else return false
        }

        return false

    }

    fun onClickRegister() {
        progressDialog!!.show()
        val shared = getPreferences(Context.MODE_PRIVATE)
        var instanceId:String?= null

        if(!isValidPhone(donorPhone!!.text.toString()))
        {
            progressDialog!!.dismiss()
            donorPhone!!.error = "Please Enter Valid Phone Number"
            donorPhone!!.requestFocus()
        }
        else if (donorName!!.text.isNullOrEmpty()){
            progressDialog!!.dismiss()
            donorName!!.error = "Please Enter Name"
            donorName!!.requestFocus()
        }
        else if (chosenTownship== null){
            progressDialog!!.dismiss()
            android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Information")
                    .setMessage("Please Choose Your Address")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Close", DialogInterface.OnClickListener {
                        dialog, whichButton ->

                    }).show()
        }
        else{
            if(shared.getString("instance_id","") != null)
            {

                instanceId= PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("instance_id", "default String")
            }
            var donorModel:DonorInfoModel
            if (fontFlag!!){
                donorModel = DonorInfoModel(donorName!!.text.toString(),
                        donorEmail!!, donorImageUrl!!,
                        donorPhone!!.text.toString(),
                        chosenCity!!, chosenTownship!!, instanceId!!)
            }
            else{
                donorModel = DonorInfoModel(Rabbit.zg2uni(donorName!!.text.toString()),
                        donorEmail!!, donorImageUrl!!,
                        donorPhone!!.text.toString(),
                        Rabbit.zg2uni(chosenCity!!),
                        Rabbit.zg2uni(chosenTownship!!), instanceId!!)
            }
            donorModel.noti_count="0"
            donorModel.fb_id = currentUser!!.providerData[1].uid
            donorReference.child(donorId).setValue(donorModel.toHashMap())
            android.os.Handler().postDelayed({
                val i = android.content.Intent(this, DonorNavigationDrawerActivity::class.java)
                startActivity(i)
                progressDialog!!.dismiss()
                this.finish()
            },1500)
        }


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if(item.itemId==android.R.id.home){
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
