package com.project.mt.dc.donor.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.mikhaellopez.circularimageview.CircularImageView
import com.project.mt.dc.R
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.service.FirebaseStorageService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.service.ShowAddressDialogService
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class EditProfileActivity : AppCompatActivity() {

    lateinit var img_donorImage: CircularImageView
    lateinit var txt_donorPhone: EditText
    lateinit var txt_donorName: EditText
    lateinit var btn_donorAddress: TextView
    lateinit var donorModel: DonorInfoModel
    lateinit var img_arrowDown: ImageView
    lateinit var txt_donorbio:EditText
    var progressDialog: ProgressDialog? = null
    var flag: Boolean? = null
    val methodUtil = MethodUtil()
    var fontFlag: Boolean? = null
    val showAddressDialogService = ShowAddressDialogService("profile_setup", this)

    var donorTownship: String? = null
    var donorCity: String? = null
    var myImageBitmap: Bitmap? = null
    var bioFlag:Boolean=false
    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

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
        setContentView(R.layout.activity_donor_editprofile)

        MDetect.init(this)
        fontFlag = MDetect.isUnicode()
        flag = false

        showAddressDialogService.setData()
        val toolbar = findViewById(R.id.toolbar_editprofile) as Toolbar

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Saving")

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
        title = "Edit Profile"


        img_arrowDown = findViewById(R.id.img_arrowdown) as ImageView
        img_donorImage = findViewById(R.id.img_donorimage) as CircularImageView
        txt_donorName = findViewById(R.id.txt_donorname) as EditText
        txt_donorPhone = findViewById(R.id.txt_donorphone) as EditText
        txt_donorbio=findViewById(R.id.txt_donorbio)as EditText
        btn_donorAddress = findViewById(R.id.lab_donoraddress) as TextView


        donorModel = intent.getSerializableExtra("donor") as DonorInfoModel
        bioFlag = donorModel.donor_bio!=null

        Glide.with(this)
                .load(donorModel.donor_image)
                .bitmapTransform(CropCircleTransformation(this))
                .into(img_donorImage)

        if (fontFlag!!) {
            txt_donorName.setText(donorModel.donor_name)
            txt_donorPhone.setText(donorModel.donor_phone)
            btn_donorAddress.text = donorModel.donor_township + " . " + donorModel.donor_city
            if (donorModel.donor_bio.isNullOrEmpty()){
                txt_donorbio.setText(" ")
            }
            else{
                txt_donorbio.setText(donorModel.donor_bio)
            }
        } else {
            txt_donorName.setText(Rabbit.uni2zg(donorModel.donor_name))
            txt_donorPhone.setText(Rabbit.uni2zg(donorModel.donor_phone))
            if (donorModel.donor_bio.isNullOrEmpty()){
                txt_donorbio.setText(" ")
            }
            else{
                txt_donorbio.setText(Rabbit.uni2zg(donorModel.donor_bio))
            }
            btn_donorAddress.text = Rabbit.uni2zg(donorModel.donor_township + " . " + donorModel.donor_city)
        }

        donorTownship = donorModel.donor_township
        donorCity = donorModel.donor_city

        btn_donorAddress.setOnClickListener({
            showAddressDialogService.setCityDialog()
        })

        img_donorImage.setOnClickListener({
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 888)
        })

        img_arrowDown.setOnClickListener({
            showAddressDialogService.setCityDialog()
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            val mySelectedImage = data.data
            try {

                myImageBitmap = getBitmap(this.contentResolver, mySelectedImage)
                Glide.with(this).load(methodUtil.bitmapToByte((myImageBitmap)!!))
                        .asBitmap()
                        .override(100, 100)
                        .fitCenter().into(img_donorImage)

            } catch (e: Exception) {

            }
        }
    }

    @Subscribe
    fun getChosenAddress(addressEvent: Event.AddressEvent) {
        flag = true
        donorCity = addressEvent.city
        donorTownship = addressEvent.township

        btn_donorAddress.text = addressEvent.township + " . " + addressEvent.city
    }

    @Subscribe
    fun getImageUrl(requestModelEvent: Event.RequestModelEvent) {
        if (requestModelEvent.caller == "donoreditprofile") {
            donorModel.donor_image = requestModelEvent.requestModel!!.request_image
            updateToFirebase()
        }

    }

    fun updateToFirebase() {

        if (fontFlag!!) {
            donorModel.donor_name = txt_donorName.text.toString()
            donorModel.donor_phone = txt_donorPhone.text.toString()
            donorModel.donor_city = donorCity
            donorModel.donor_township = donorTownship
            if (bioFlag!!){
                donorModel.donor_bio=txt_donorbio.text.toString()
            }
        } else {
            donorModel.donor_name = Rabbit.zg2uni(txt_donorName.text.toString())
            donorModel.donor_phone = Rabbit.zg2uni(txt_donorPhone.text.toString())
            if (flag!!) {
                donorModel.donor_city = Rabbit.zg2uni(donorCity)
                donorModel.donor_township = Rabbit.zg2uni(donorTownship)
                if (bioFlag!!) {
                    donorModel.donor_bio = Rabbit.zg2uni(txt_donorbio.text.toString())
                }
            } else {
                donorModel.donor_city = donorCity
                donorModel.donor_township = donorTownship
                if (bioFlag!!) {
                    donorModel.donor_bio = txt_donorbio.text.toString()
                }

            }
        }
        if (bioFlag!!) {
            FirebaseWriteService().updateChild("donor", currentUser, donorModel.toEditHashMap())
        }
        else{
            FirebaseWriteService().updateChild("donor", currentUser, donorModel.toHashMap())
        }
        FirebaseWriteService().updateChildIndirect("donateitems", "donor_id", currentUser, "donor_township", donorTownship!!)
        progressDialog!!.dismiss()
        Toast.makeText(this, "Edited", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun isValidPhone(phone: String): Boolean {

        if (phone.length > 6) {

            val phUtil = PhoneNumberUtil.getInstance()
            val parseNumber = phUtil.parse(phone, "MM")


            if (phUtil.isValidNumber(parseNumber)) {
                txt_donorPhone.setText("+${parseNumber.countryCode} ${parseNumber.nationalNumber}")

                return true

            } else return false
        }

        return false

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_check, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == android.R.id.home) {
                finish()
            }
            if (item.itemId == R.id.action_check) {

                if (txt_donorName.text.isNullOrEmpty()) {
                    txt_donorName.error = "Please Enter Your Name"
                    txt_donorName.requestFocus()
                } else if (txt_donorPhone.text.isNullOrEmpty()) {
                    txt_donorPhone.error = "Please Enter Your Phone Number"
                    txt_donorPhone.requestFocus()
                } else if (!isValidPhone(txt_donorPhone!!.text.toString())) {
                    txt_donorPhone.error = "Please Enter Valid Phone Number"
                    txt_donorPhone.requestFocus()
                } else if (myImageBitmap != null) {
                    progressDialog!!.show()
                    FirebaseStorageService().upload1("donor_images", currentUser, myImageBitmap!!, "donoreditprofile")
                } else {
                    progressDialog!!.show()
                    updateToFirebase()
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }


}
