package com.project.mt.dc.charity.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.project.mt.dc.R
import com.project.mt.dc.charity.fragment.DatePickerFragment
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.service.FirebaseStorageService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.util.MethodUtil
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.MMEditText
import me.myatminsoe.mdetect.MMTextView
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class RequestFormActivity : AppCompatActivity() {

    lateinit var txt_requestPlace: MMEditText
    lateinit var txt_requestDescription: MMEditText
    lateinit var txt_requestDate: TextView
    lateinit var txt_requestPhone: TextView
    lateinit var txt_requestLocation:MMEditText
    lateinit var img_requestImage: ImageView
    lateinit var caller: String
    lateinit var progress: ProgressDialog

    lateinit var string_placeError:String
    lateinit var string_imageError:String
    lateinit var string_locationError:String
    lateinit var string_dateError:String
    lateinit var string_descriptionError:String
    lateinit var string_date:String

    lateinit var relative_upload: RelativeLayout
    lateinit var linear_upload: LinearLayout

    var fontFlag:Boolean?= null

    var myImageBitmap: Bitmap? = null
    var requestModel: RequestModel? = null
    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid


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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MDetect.init(this)
        fontFlag=MDetect.isUnicode()

        if (fontFlag as Boolean){
            string_placeError=getString(R.string.charity_post_place_error)
            string_imageError=getString(R.string.charity_post_photo_error)
            string_descriptionError=getString(R.string.charity_post_description_error)
            string_locationError=getString(R.string.charity_post_location_error)
            string_dateError=getString(R.string.charity_post_date_error)
        }
        else{
            string_placeError=Rabbit.uni2zg(getString(R.string.charity_post_place_error))
            string_imageError=Rabbit.uni2zg(getString(R.string.charity_post_photo_error))
            string_descriptionError=Rabbit.uni2zg(getString(R.string.charity_post_description_error))
            string_locationError=Rabbit.uni2zg(getString(R.string.charity_post_location_error))
            string_dateError=Rabbit.uni2zg(getString(R.string.charity_post_date_error))
        }

        setContentView(R.layout.activity_charity_request_form)
        val toolbar = findViewById(R.id.toolbar_requestform) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)

        relative_upload=findViewById(R.id.relative_upload)as RelativeLayout
        linear_upload=findViewById(R.id.linear_upload)as LinearLayout
        txt_requestDate = findViewById(R.id.lab_requestdatepick) as MMTextView
        txt_requestLocation=findViewById(R.id.lab_requestlocation)as MMEditText
        txt_requestPlace = findViewById(R.id.txt_requestplace) as MMEditText
        txt_requestDescription = findViewById(R.id.txt_requestdescription) as MMEditText
        img_requestImage = findViewById(R.id.img_requestimage) as ImageView
        val img_reqesstDate=findViewById(R.id.img_requestdatepick)as ImageView

        /*txt_requestDate.typeface=fontUtil.medium_font
        txt_requestLocation.typeface=fontUtil.medium_font
        txt_requestDescription.typeface=fontUtil.regular_font
        txt_requestPlace.typeface=fontUtil.title_font*/
        if(fontFlag!!){
            string_date=getString(R.string.charity_post_date)
            title=getString(R.string.charity_post)
        }
        else{
            string_date=Rabbit.uni2zg(getString(R.string.charity_post_date))
            title=Rabbit.uni2zg(getString(R.string.charity_post))
        }
        txt_requestDate.text=string_date
        caller = intent.getStringExtra("caller")
        if (caller == "edit") {
            linear_upload.visibility=View.GONE
            requestModel = intent.getSerializableExtra("requestmodel") as RequestModel
            if(fontFlag!!) {

                txt_requestPlace.setText(requestModel!!.request_place)
                txt_requestDescription.setText(requestModel!!.request_description)
                txt_requestLocation.setText(requestModel!!.request_location)
            }
            else{
                txt_requestPlace.setText(Rabbit.uni2zg(requestModel!!.request_place))
                txt_requestDescription.setText(Rabbit.uni2zg(requestModel!!.request_description))
                txt_requestLocation.setText(Rabbit.uni2zg(requestModel!!.request_location))
            }
            txt_requestDate.text = requestModel!!.request_date
            Glide.with(this)
                    .load(requestModel!!.request_image)
                    .into(img_requestImage)

        }

        progress = ProgressDialog(this)
        progress.setMessage("Please Wait")

        img_requestImage.setOnClickListener({
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 777)
        })

        txt_requestDate.setOnClickListener({
            var datePicker = DatePickerFragment(this,txt_requestDate)
            datePicker.show(this.supportFragmentManager, "datePicker")

        })

        img_reqesstDate.setOnClickListener({
            var datePicker = DatePickerFragment(this,txt_requestDate)
            datePicker.show(this.supportFragmentManager, "datePicker")

        })

        relative_upload.setOnClickListener({
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 777)
        })

        FirebaseReadService().getCharity(currentUser)

    }

    @Subscribe
    fun getCharity(charityModelEvent: Event.CharityModelEvent) {
        if (charityModelEvent != null) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 777 && resultCode == Activity.RESULT_OK && data != null) {
            val mySelectedImage = data.data
            try {

                myImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, mySelectedImage)
                linear_upload.visibility= View.GONE
                Glide.with(this).load(MethodUtil().bitmapToByte((myImageBitmap)!!))
                        .asBitmap()
                        .centerCrop().into(img_requestImage)
                img_requestImage.setImageBitmap(myImageBitmap)
            } catch (e: Exception) {

            }
        }
    }

    @Subscribe
    fun getImageUrl(requestModelEvent: Event.RequestModelEvent) {
        if(requestModelEvent.caller=="requestform") {
            val requestModel = RequestModel()

            val place = txt_requestPlace.text.toString()
            val descrition = txt_requestDescription.text.toString()
            val date = txt_requestDate.text.toString()
            val location = txt_requestLocation.text.toString()


            if (place.isNullOrEmpty()) {
                txt_requestPlace.setError("Enter Place of Your Donation")
            } else {
                if (fontFlag!!) {

                    requestModel.request_place = place
                    requestModel.request_description = descrition
                    requestModel.request_location = location


                } else {

                    requestModel.request_place = Rabbit.zg2uni(place)
                    requestModel.request_description = Rabbit.zg2uni(descrition)
                    requestModel.request_location = Rabbit.zg2uni(location)
                }
                requestModel.request_date = date
                requestModel.request_image = requestModelEvent.requestModel!!.request_image
                requestModel.post_time = MethodUtil().getCurrentTime()
                requestModel.charity_id = currentUser
                requestModel.request_id = requestModelEvent.requestModel!!.request_id
                requestModel.request_status = "requesting"
                FirebaseWriteService().setCharityRequest(currentUser, requestModel.request_id!!, requestModel)

                progress.dismiss()
                Toast.makeText(this, "Your donation has been saved.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }


    fun checkEmptyFields():Boolean{
        if (txt_requestPlace.text.isNullOrEmpty()) {
            txt_requestPlace.error = string_placeError
            txt_requestPlace.requestFocus()
        }else if (txt_requestDescription.text.isNullOrEmpty()) {
            txt_requestDescription.error = string_descriptionError
            txt_requestDescription.requestFocus()
        }else if (txt_requestLocation.text.isNullOrEmpty()) {
            txt_requestLocation.error = string_locationError
            txt_requestLocation.requestFocus()
        }else if (txt_requestDate.text.equals(string_date)) {
            android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Information")
                    .setMessage(string_dateError)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, whichButton ->

                    }).show()
        }
        else{
            return true
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


                var request_id: String

                if (caller == "edit") {
                    request_id = requestModel!!.request_id!!
                    if(myImageBitmap!= null) {
                        if (checkEmptyFields()) {
                            FirebaseStorageService().upload1("request_images", request_id!!, myImageBitmap!!, "requestform")
                        }
                    }
                    else{
                        if (checkEmptyFields()) {
                            FirebaseStorageService().upload("request_images", request_id!!, img_requestImage, "requestform")
                        }
                    }
                } else {
                    if (myImageBitmap != null) {
                        request_id = FirebaseDatabase.getInstance().getReference(currentUser)
                                .child("request_post")
                                .push()
                                .key
                        if (checkEmptyFields()) {
                            progress.show()
                            FirebaseStorageService().upload1("request_images", request_id!!, myImageBitmap!!,"requestform")
                        }
                    } else {
                        progress.dismiss()
                        android.support.v7.app.AlertDialog.Builder(this)
                                .setTitle("Information")
                                .setMessage(string_imageError)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Close", DialogInterface.OnClickListener {
                                    dialog, whichButton ->

                                }).show()
                    }
                }


            }
        }
        return super.onOptionsItemSelected(item)
    }
}
