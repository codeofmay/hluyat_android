package com.project.mt.dc.charity.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.mikhaellopez.circularimageview.CircularImageView
import com.project.mt.dc.R
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.service.FirebaseStorageService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class EditProfileActivity : AppCompatActivity() {

    lateinit var img_charityImage: CircularImageView
    lateinit var txt_charityPhone: EditText
    lateinit var txt_charityName: EditText
    lateinit var txt_charityDescription: EditText
    lateinit var donorModel: DonorInfoModel
    lateinit var img_arrowDown: ImageView
    lateinit var txt_charityPassword: EditText
    lateinit var txt_charityYear: EditText
    lateinit var lab_charityEmail: TextView

    var progressDialog: ProgressDialog? = null
    var charityModel=CharityModel()

    val methodUtil = MethodUtil()
    var fontFlag: Boolean? = null

    var myImageBitmap: Bitmap? = null

    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    val user=FirebaseAuth.getInstance().currentUser

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
        setContentView(R.layout.activity_edit_profile)

        MDetect.init(this)
        fontFlag = MDetect.isUnicode()

        val toolbar = findViewById(R.id.toolbar_editprofile) as Toolbar

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Saving")

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
        title = "Edit Profile"

        img_charityImage = findViewById(R.id.img_charityimage) as CircularImageView
        txt_charityName = findViewById(R.id.txt_charityname) as EditText
        txt_charityPhone = findViewById(R.id.txt_charityphone) as EditText
        txt_charityYear=findViewById(R.id.txt_charityyear)as EditText
        txt_charityDescription = findViewById(R.id.txt_charitydescription) as EditText
        txt_charityPassword = findViewById(R.id.txt_charitypassword) as EditText
        lab_charityEmail = findViewById(R.id.lab_charityemail) as TextView

        charityModel = intent.getSerializableExtra("charitymodel") as CharityModel


        Glide.with(this)
                .load(charityModel!!.charity_image)
                .bitmapTransform(CropCircleTransformation(this))
                .into(img_charityImage)

        if (fontFlag!!) {
            txt_charityName.setText(charityModel.charity_name)
            txt_charityDescription.setText(charityModel.charity_description)


        } else {
            txt_charityName.setText(Rabbit.uni2zg(charityModel.charity_name))
            txt_charityDescription.setText(Rabbit.uni2zg(charityModel.charity_description))
        }
        txt_charityYear.setText(charityModel.charity_year)
        txt_charityPhone.setText(charityModel.charity_phone)
        lab_charityEmail.text = charityModel.charity_email
        txt_charityPassword.setText(charityModel.charity_password)


        img_charityImage.setOnClickListener({
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 888)
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            val mySelectedImage = data.data
            try {

                myImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, mySelectedImage)
                Glide.with(this).load(methodUtil.bitmapToByte((myImageBitmap)!!))
                        .asBitmap()
                        .override(100, 100)
                        .centerCrop().into(img_charityImage)
            } catch (e: Exception) {

            }
        }
    }


    @Subscribe
    fun getImageUrl(requestModelEvent: Event.RequestModelEvent) {
        if (requestModelEvent.caller == "charityeditprofile") {
            charityModel.charity_image = requestModelEvent.requestModel!!.request_image
            checkForPasswordUpdate()
        }

    }

    fun checkForPasswordUpdate(){

        if(txt_charityPassword.text.toString()!=charityModel.charity_password){
            val credential = EmailAuthProvider.getCredential(charityModel.charity_email!!, charityModel.charity_password!!)
            user?.reauthenticate(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.updatePassword(txt_charityPassword.text.toString()).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    updateToFirebase()
                                } else {
                                    progressDialog!!.dismiss()
                                    txt_charityPassword!!.error = "Invalid Password.The Password should be at least 6 characters."
                                    txt_charityPassword!!.requestFocus()
                                }
                            }
                        } else {
                            progressDialog!!.dismiss()
                            txt_charityPassword!!.error = task.exception.toString()
                            txt_charityPassword!!.requestFocus()
                        }
                    }
        }
        else{
            updateToFirebase()
        }
    }
    fun updateToFirebase() {

        if (fontFlag!!) {
            charityModel.charity_name = txt_charityName.text.toString()
            charityModel.charity_description = txt_charityDescription.text.toString()
        } else {
            charityModel.charity_name = Rabbit.zg2uni(txt_charityName.text.toString())
            charityModel.charity_description = Rabbit.zg2uni(txt_charityDescription.text.toString())
        }
        charityModel.charity_phone=txt_charityPhone.text.toString()
        charityModel.charity_year=txt_charityYear.text.toString()
        charityModel.charity_password=txt_charityPassword.text.toString()

        FirebaseWriteService().updateChild("charity", currentUser, charityModel.toEditHashMap())
        progressDialog!!.dismiss()
        Toast.makeText(this, "Edited", Toast.LENGTH_SHORT).show()
        finish()
    }


    fun isValidPhone(phone: String): Boolean {

        if (phone.length > 6) {

            val phUtil = PhoneNumberUtil.getInstance()
            val parseNumber = phUtil.parse(phone, "MM")


            if (phUtil.isValidNumber(parseNumber)) {
                txt_charityPhone.setText("+${parseNumber.countryCode} ${parseNumber.nationalNumber}")

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

                if (txt_charityName.text.isNullOrEmpty()) {
                    txt_charityName.error = "Please Enter Charity Name"
                    txt_charityName.requestFocus()
                }else if (txt_charityYear.text.isNullOrEmpty()) {
                    txt_charityYear.error = "Please Enter the Year When the Charity Started"
                    txt_charityYear.requestFocus()
                }else if (txt_charityDescription.text.isNullOrEmpty()) {
                    txt_charityDescription.error = "Please Enter Charity Description"
                    txt_charityDescription.requestFocus()
                }else if (txt_charityPhone.text.isNullOrEmpty()) {
                    txt_charityPhone.error = "Please Enter Charity Phone Number"
                    txt_charityPhone.requestFocus()
                } else if (!isValidPhone(txt_charityPhone!!.text.toString())) {
                    txt_charityPhone.error = "Please Enter Valid Phone Number"
                    txt_charityPhone.requestFocus()
                }else if (txt_charityPassword.text.isNullOrEmpty()) {
                    txt_charityPassword.error = "Please Enter Charity Password"
                    txt_charityPassword.requestFocus()
                }
                else if (myImageBitmap != null) {
                    progressDialog!!.show()
                    FirebaseStorageService().upload1("charity_images", currentUser, myImageBitmap!!, "charityeditprofile")
                } else {
                    progressDialog!!.show()
                    checkForPasswordUpdate()
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }

}
