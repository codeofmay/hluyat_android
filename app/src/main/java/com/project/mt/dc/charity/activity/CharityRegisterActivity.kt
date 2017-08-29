package com.project.mt.dc.charity.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil.setContentView
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.FirebaseDatabase
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.project.mt.dc.R
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.CharityModel
import com.project.mt.dc.service.FirebaseStorageService
import com.project.mt.dc.service.ShowAddressDialogService
import com.project.mt.dc.util.MethodUtil
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.ByteArrayOutputStream


class CharityRegisterActivity : AppCompatActivity() {

    lateinit var txt_charityName: TextView
    lateinit var txt_charityPhone: TextView
    lateinit var txt_charityCity: TextView
    lateinit var txt_charityEmail: TextView
    lateinit var progress: ProgressDialog
    lateinit var img_charityImage: ImageView
    lateinit var img_arrowdown:ImageView
    lateinit var btn_register: LinearLayout
    val firebaseAuth = FirebaseAuth.getInstance()
    var myImageBitmap: Bitmap? = null
    var fontFlag:Boolean?=null
    var city:String?= null
    var methodUtil=MethodUtil()


    override fun onResume() {
        super.onResume()

        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    fun onClickImage(v: View) {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, 888)
    }

    fun isValidPhone(phone:String):Boolean{

        if(phone.length>6) {

            val phUtil = PhoneNumberUtil.getInstance()
            val parseNumber = phUtil.parse(phone, "MM")

            if(phUtil.isValidNumber(parseNumber)){
                txt_charityPhone!!.text = "+${parseNumber.countryCode} ${parseNumber.nationalNumber}"

                return true

            }else return false
        }

        return false

    }

    fun onClickRequest() {

        progress.show()
        if (txt_charityName.text.isNullOrEmpty()) {
            progress!!.dismiss()
            txt_charityName!!.error = "Please Enter Charity Name"
            txt_charityName!!.requestFocus()
        }
        else if (txt_charityEmail.text.isNullOrEmpty()) {
            progress!!.dismiss()
            txt_charityEmail!!.error = "Please Enter Email"
            txt_charityEmail!!.requestFocus()
        }
        else if (txt_charityPhone.text.isNullOrEmpty()) {
            progress!!.dismiss()
            txt_charityPhone!!.error = "Please Enter Charity Phone Number"
            txt_charityPhone!!.requestFocus()
        }
        else if(!isValidPhone(txt_charityPhone!!.text.toString()))
        {
            progress.dismiss()
            txt_charityPhone!!.error = "Please Enter Valid Phone Number"
        }
        else if (txt_charityCity.text.toString()==city){

            val dialog=android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Information")
                    .setMessage("Please choose the city where your charity locate.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, whichButton ->
                    })
            progress.dismiss()
            dialog.show()
        }
        else if (myImageBitmap == null) {
            val dialog=android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Information")
                    .setMessage("Please choose the logo or image that represent your charity.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, whichButton ->
                    })
            progress.dismiss()
            dialog.show()
        } else {
            firebaseAuth.fetchProvidersForEmail(txt_charityEmail.text.toString()).addOnCompleteListener { task ->
                try {

                    if (!task.result.providers!!.isEmpty()) {
                        progress.dismiss()
                        txt_charityEmail!!.error = "The Email is already reistered by other user."
                        txt_charityEmail!!.requestFocus()
                    } else {
                        val requestReference = FirebaseDatabase.getInstance().getReference("request_charities")
                        val key = requestReference.push().key
                        FirebaseStorageService().upload1("charity_images", key, myImageBitmap!!,"charityregister")
                    }
                }
                catch (e: Exception) {
                    try {
                        throw task.exception!!!!
                    }
                    catch (e: FirebaseAuthInvalidCredentialsException) {
                        progress.dismiss()
                        txt_charityEmail!!.error = "The Email is invalid"

                    }  catch (e: Exception) {
                        progress.dismiss()
                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                    }

                }
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MDetect.init(this)
        fontFlag=MDetect.isUnicode()
        setContentView(R.layout.activity_charity_register)
        val showAddressDialogService=ShowAddressDialogService("charityprofile",this)
        showAddressDialogService.setData()

        if (fontFlag as Boolean){
            city=getString(R.string.choose_city)
        }
        else{
            city=Rabbit.uni2zg(getString(R.string.choose_city))
        }
        val toolbar = findViewById(R.id.toolbar_charityrequest) as Toolbar
        setSupportActionBar(toolbar)

        if(supportActionBar!= null){
            supportActionBar!!.title="Request A Charity"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }



        txt_charityName = findViewById(R.id.txt_charityname) as TextView
        txt_charityEmail = findViewById(R.id.txt_charityemail) as TextView
        txt_charityPhone = findViewById(R.id.txt_charityphone) as TextView
        txt_charityCity = findViewById(R.id.txt_charitycity) as TextView
        img_charityImage = findViewById(R.id.img_charityimage) as ImageView
        img_arrowdown=findViewById(R.id.img_arrowdown)as ImageView
        btn_register=findViewById(R.id.btn_charityregister)as LinearLayout

        if (fontFlag!!){
            txt_charityCity.text=getString(R.string.choose_city)
        }
        else{
            txt_charityCity.text=Rabbit.uni2zg(getString(R.string.choose_city))
        }
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait")
        txt_charityCity.setOnClickListener({
            showAddressDialogService.setCityDialog()
        })
        img_arrowdown.setOnClickListener({
            showAddressDialogService.setCityDialog()
        })
        btn_register.setOnClickListener({
            onClickRequest()
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 888 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val mySelectedImage = data.data
            try {
                myImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, mySelectedImage)

                Glide.with(this)
                        .load(methodUtil.bitmapToByte((myImageBitmap)!!)).asBitmap()
                        .into(img_charityImage)
            } catch (e: Exception) {

            }
        }
    }


    @Subscribe
    fun getChoseCity(searchAddressEvent: Event.SearchAddressEvent) {
        txt_charityCity.text = searchAddressEvent.township
    }

    @Subscribe
    fun getImageUrl(requestModelEvent: Event.RequestModelEvent) {

        if(requestModelEvent.caller=="charityregister") {

            val charityModel = CharityModel()

            if(fontFlag!!) {
                charityModel.charity_email = txt_charityEmail.text.toString()
                charityModel.charity_name = txt_charityName.text.toString()
                charityModel.charity_city = txt_charityCity.text.toString()
            }
            else{
                charityModel.charity_email = Rabbit.zg2uni(txt_charityEmail.text.toString())
                charityModel.charity_name = Rabbit.zg2uni(txt_charityName.text.toString())
                charityModel.charity_city = Rabbit.zg2uni(txt_charityCity.text.toString())
            }
            charityModel.charity_image = requestModelEvent.requestModel!!.request_image
            charityModel.charity_phone = txt_charityPhone.text.toString()
            charityModel.request_id = requestModelEvent.requestModel!!.request_id
            FirebaseDatabase.getInstance().getReference("request_charities").child(requestModelEvent.requestModel!!.request_id)
                    .setValue(charityModel.toRequestHashMap())

            val dialog=android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Received")
                    .setMessage("We have received the charity information.We will contact you")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, whichButton ->
                        finish()
                    })
            progress.dismiss()
            dialog.show()
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

