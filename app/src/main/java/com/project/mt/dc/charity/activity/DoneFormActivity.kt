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
import com.project.mt.dc.R
import com.project.mt.dc.charity.fragment.DatePickerFragment
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.service.FirebaseStorageService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.MMEditText
import me.myatminsoe.mdetect.MMTextView
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DoneFormActivity : AppCompatActivity() {


    lateinit var txt_donePlace: TextView
    lateinit var txt_doneDescription: TextView
    lateinit var txt_doneDate: TextView
    lateinit var txt_donePhone: TextView
    lateinit var txt_doneLocation: TextView
    lateinit var img_doneImage: ImageView
    lateinit var progress: ProgressDialog
    var fontFlag:Boolean?= null
    var requestModel:RequestModel?= null
    var myImageBitmap: Bitmap? = null
    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    var donorList:ArrayList<NotificationModel>?=null
    val firebaseWriteService=FirebaseWriteService()
    val firebaseReadService=FirebaseReadService()
    var progressDialog:ProgressDialog?=null

    lateinit var string_placeError:String
    lateinit var string_imageError:String
    lateinit var string_locationError:String
    lateinit var string_dateError:String
    lateinit var string_descriptionError:String
    lateinit var string_date:String
    lateinit var relative_upload: RelativeLayout
    lateinit var linear_upload: LinearLayout

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
        setContentView(R.layout.activity_done_form)

        MDetect.init(this)
        fontFlag= MDetect.isUnicode()


        if (fontFlag as Boolean){
            string_placeError=getString(R.string.charity_done_place_error)
            string_imageError=getString(R.string.charity_done_photo_error)
            string_descriptionError=getString(R.string.charity_done_description_error)
            string_locationError=getString(R.string.charity_done_location_error)
            string_dateError=getString(R.string.charity_done_date_error)
        }
        else{
            string_placeError=Rabbit.uni2zg(getString(R.string.charity_done_place_error))
            string_imageError=Rabbit.uni2zg(getString(R.string.charity_done_photo_error))
            string_descriptionError=Rabbit.uni2zg(getString(R.string.charity_done_description_error))
            string_locationError=Rabbit.uni2zg(getString(R.string.charity_done_location_error))
            string_dateError=Rabbit.uni2zg(getString(R.string.charity_done_date_error))
        }


        val fontUtil= FontUtil(this)

        val title=getString(R.string.charity_done_title)

        val toolbar = findViewById(R.id.toolbar_doneform) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)

        progressDialog= ProgressDialog(this)
        progressDialog!!.setMessage("Please Wait")

        if(fontFlag!!){
            supportActionBar!!.title=title
        }
        else{
            supportActionBar!!.title=Rabbit.uni2zg(title)
        }
        relative_upload=findViewById(R.id.relative_upload)as RelativeLayout
        linear_upload=findViewById(R.id.linear_upload)as LinearLayout
        txt_doneDate = findViewById(R.id.lab_doneDatepick) as MMTextView
        txt_doneLocation=findViewById(R.id.lab_donelocation)as MMEditText
        txt_donePlace = findViewById(R.id.txt_doneplace) as MMEditText
        txt_doneDescription = findViewById(R.id.txt_donedescription) as MMEditText
        img_doneImage = findViewById(R.id.img_doneimage) as ImageView
        val img_doneDate=findViewById(R.id.img_donedatepick) as ImageView

       /* txt_doneDate.typeface=fontUtil.medium_font
        txt_doneLocation.typeface=fontUtil.medium_font
        txt_doneDescription.typeface=fontUtil.regular_font
        txt_donePlace.typeface=fontUtil.title_font*/

        requestModel = intent.getSerializableExtra("donemodel") as RequestModel
        if(fontFlag!!) {
            txt_donePlace.text = requestModel!!.request_place
            txt_doneLocation.text = requestModel!!.request_location
            string_date=getString(R.string.charity_done_date)

        }
        else{
            txt_donePlace.text = Rabbit.uni2zg(requestModel!!.request_place)
            txt_doneLocation.text = Rabbit.uni2zg(requestModel!!.request_location)
            string_date=Rabbit.uni2zg(getString(R.string.charity_done_date))
        }
        txt_doneDate.text=string_date
        txt_doneDate.setOnClickListener({
            var datePicker = DatePickerFragment(this,txt_doneDate)
            datePicker.show(this.supportFragmentManager, "datePicker")

        })
        img_doneDate.setOnClickListener({
            var datePicker = DatePickerFragment(this,txt_doneDate)
            datePicker.show(this.supportFragmentManager, "datePicker")

        })

        img_doneImage.setOnClickListener({
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 666)
        })
        relative_upload.setOnClickListener({
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 666)
        })

        firebaseReadService.getCharityDonorList(requestModel!!.charity_id!!, requestModel!!.request_id!!,"doneformdonorlist")

    }

    @Subscribe
    fun getDonorList(listEvent: Event.ListEvent<NotificationModel>) {
        if (listEvent.type == "doneformdonorlist") {
            donorList=listEvent.list
        }
    }

    @Subscribe
    fun getImageUrl(requestModelEvent: Event.RequestModelEvent) {
        if(requestModelEvent.caller =="doneform") {
            val doneModel = RequestModel()
            doneModel.request_id = requestModelEvent.requestModel!!.request_id
            doneModel.charity_id = currentUser
            doneModel.request_date = txt_doneDate.text.toString()
            doneModel.request_image = requestModelEvent.requestModel!!.request_image
            doneModel.post_time = MethodUtil().getCurrentTime()
            doneModel.request_status = "done"

            val place = txt_donePlace.text.toString()
            val description = txt_doneDescription.text.toString()
            val location = txt_doneLocation.text.toString()

            if (fontFlag!!) {

                doneModel.request_place = place
                doneModel.request_description = description
                doneModel.request_location = location


            } else {

                doneModel.request_place = Rabbit.zg2uni(place)
                doneModel.request_description = Rabbit.zg2uni(description)
                doneModel.request_location = Rabbit.zg2uni(location)
            }


            FirebaseWriteService().setDonatedItem(donorList!!, doneModel)
            progressDialog!!.dismiss()
            Toast.makeText(this, "The Donated Information had been Saved", Toast.LENGTH_LONG).show()
            finish()
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 666 && resultCode == Activity.RESULT_OK && data != null) {
            val mySelectedImage = data.data
            try {

                myImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, mySelectedImage)
                linear_upload.visibility= View.GONE
                Glide.with(this).load(MethodUtil().bitmapToByte((myImageBitmap)!!))
                        .asBitmap()
                        .centerCrop().into(img_doneImage)

            } catch (e: Exception) {

            }
        }
    }

     fun checkEmptyFields():Boolean{
       if (txt_donePlace.text.isNullOrEmpty()) {
           txt_donePlace.error = string_placeError
           txt_donePlace.requestFocus()
       }else if (txt_doneDescription.text.isNullOrEmpty()) {
           txt_doneDescription.error = string_descriptionError
           txt_doneDescription.requestFocus()
       }else if (txt_doneLocation.text.isNullOrEmpty()) {
           txt_doneLocation.error = string_locationError
           txt_doneLocation.requestFocus()
       }else if (txt_doneDate.text.equals(string_date)) {
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
        menuInflater.inflate(R.menu.menu_check,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if(item.itemId == android.R.id.home){
                finish()
            }
            if(item.itemId == R.id.action_check){

                if (myImageBitmap != null) {
                    if (checkEmptyFields()) {
                        progressDialog!!.show()
                        val done_id = requestModel!!.request_id
                        FirebaseStorageService().upload1("done_images", done_id!!, myImageBitmap!!, "doneform")
                    }
                } else {
                    progressDialog!!.dismiss()
                    android.support.v7.app.AlertDialog.Builder(this)
                            .setTitle("Information")
                            .setMessage(string_imageError)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("OK", DialogInterface.OnClickListener {
                                dialog, whichButton ->

                            }).show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
