package com.project.mt.dc.donor.activity

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil.setContentView
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.R
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.service.FirebaseRemoveService
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

class DonateFormActivity : AppCompatActivity() {

    var btn_itemCategory: MMTextView? = null
    var lab_itemAmount: MMEditText? = null
    var img_itemImage: ImageView? = null
    var township: String? = null
    var user: String? = null
    var btn_donate: Button? = null
    var btn_cancel: Button? = null
    var itemInfoModel = ItemInfoModel()
    var donorModel: DonorInfoModel? = null
    var fontFlag: Boolean? = null
    var fontUtil: FontUtil? = null
    lateinit var donateInfo: TextView
    lateinit var category_error: String
    lateinit var image_error:String
    lateinit var amount_error: String
    var myBitmap: Bitmap? = null
    lateinit var category_hint:String
    lateinit var progressDialog:ProgressDialog
    var methodUtil=MethodUtil()


    fun onClickDonate() {

        if (btn_itemCategory!!.text.toString() == category_hint) {
            val dialog=android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Information")
                    .setMessage(category_error)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Close", DialogInterface.OnClickListener {
                        dialog, whichButton ->

                    }).show()
            val message=dialog.findViewById(android.R.id.message)as TextView
            message.gravity=Gravity.CENTER
            message.setPadding(4,4,4,4)
        } else if (lab_itemAmount!!.text.isNullOrEmpty()) {
            lab_itemAmount!!.error = amount_error

        } else if (myBitmap == null) {
            android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Information")
                    .setMessage(image_error)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Close", DialogInterface.OnClickListener {
                        dialog, whichButton ->

                    }).show()

        } else {
            progressDialog.show()
            val currentTime = MethodUtil().getCurrentTime()
            val currentDate = MethodUtil().getCurrentDate()

            if (fontFlag!!) {
                itemInfoModel.item_amount = lab_itemAmount!!.text.toString()
                itemInfoModel.item_category = btn_itemCategory!!.text.toString()
            } else {
                itemInfoModel.item_amount = Rabbit.zg2uni(lab_itemAmount!!.text.toString())
                itemInfoModel.item_category = Rabbit.zg2uni(btn_itemCategory!!.text.toString())
            }

            itemInfoModel.item_image = "R.drawable.img.jpg"
            itemInfoModel.item_time = currentTime
            itemInfoModel.donor_id = user
            itemInfoModel.item_date = currentDate
            itemInfoModel.item_status = "Donating"
            itemInfoModel.accept_notiId = "null"
            itemInfoModel.donor_township = donorModel!!.donor_township

            val donorReference = FirebaseDatabase.getInstance().getReference("donateitems")
            val item_id = donorReference.push().key

            FirebaseStorageService().upload("item_images", item_id, img_itemImage!!, "donateform")
        }


    }

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
        setContentView(com.project.mt.dc.R.layout.activity_donor_donateform)
        val toolbar = findViewById(R.id.toolbar_donordonate) as Toolbar
        setSupportActionBar(toolbar)

        progressDialog= ProgressDialog(this)
        progressDialog.setMessage("Please Wait")

        MDetect.init(this)
        fontFlag = MDetect.isUnicode()

        fontUtil = FontUtil(this)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)

        btn_itemCategory = findViewById(R.id.btn_itemcategory) as MMTextView
        lab_itemAmount = findViewById(R.id.lab_itemamount) as MMEditText
        img_itemImage = findViewById(R.id.img_itemimage) as ImageView
        donateInfo = findViewById(R.id.lab_donateinfo) as TextView
        val img_arrowdonw = findViewById(R.id.img_arrowdown) as ImageView

        btn_itemCategory!!.typeface = fontUtil!!.regular_font
        /*lab_itemAmount!!.typeface = fontUtil!!.regular_font*/

        if (fontFlag!!) {
            title = getString(R.string.donor_post)
            category_hint=getString(R.string.donor_post_category)
            btn_itemCategory!!.text = category_hint
            donateInfo.text = getString(R.string.donor_donateinfo)

            category_error = getString(R.string.donor_post_category_error)
            image_error = getString(R.string.donor_post_image_errror)
            amount_error = getString(R.string.donor_post_amount_error)
        } else {
            title = Rabbit.uni2zg(getString(R.string.donor_post))
            category_hint=Rabbit.uni2zg(getString(R.string.donor_post_category))
            btn_itemCategory!!.text = category_hint
            donateInfo.text = Rabbit.uni2zg(getString(R.string.donor_donateinfo))

            category_error = Rabbit.uni2zg(getString(R.string.donor_post_category_error))
            image_error = Rabbit.uni2zg(getString(R.string.donor_post_image_errror))
            amount_error = Rabbit.uni2zg(getString(R.string.donor_post_amount_error))
        }

        user = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().getReference("donor").child(user).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(donorSnapShot: DataSnapshot?) {
                township = donorSnapShot!!.child("donor_township").value.toString()
            }
        })

        btn_itemCategory!!.setOnClickListener({
            com.project.mt.dc.service.ShowCategoryDialogService(this, "post")
        })

        img_arrowdonw.setOnClickListener({
            com.project.mt.dc.service.ShowCategoryDialogService(this, "post")
        })


        img_itemImage!!.setOnClickListener({
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 999)
        })

        FirebaseReadService().getDonor(user!!)

    }

    @org.greenrobot.eventbus.Subscribe
    fun getChosenCategory(searchCategoryEvent: Event.SearchCategoryEvent) {
        val category = searchCategoryEvent.category
        btn_itemCategory!!.text = category


    }

    @Subscribe
    fun getImageUrl(requestModelEvent: Event.RequestModelEvent) {
        if (requestModelEvent.caller == "donateform") {
            val requestModel = requestModelEvent.requestModel
            itemInfoModel.item_key = requestModel!!.request_id
            itemInfoModel.item_image = requestModel.request_image
            FirebaseWriteService().setDonateItem(itemInfoModel)
            progressDialog.dismiss()
            Toast.makeText(this, "Thar Du", android.widget.Toast.LENGTH_SHORT).show()
            this.finish()
        }
    }

    @Subscribe
    fun getDonor(donorModelEvent: Event.DonorModelEvent) {
        donorModel = donorModelEvent.donorModel
    }

    //image upload
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 999 && resultCode == RESULT_OK && intent != null) {
            val mySelectImage = intent.data

            try {
                myBitmap = getBitmap(this.contentResolver, mySelectImage)
                Glide.with(this).load(methodUtil.bitmapToByte((myBitmap)!!))
                        .asBitmap()
                        .centerCrop().into(img_itemImage)
            } catch (e: Exception) {
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            finish()
        }

        if (item!!.itemId == R.id.action_check) {
            onClickDonate()
        }
        return super.onOptionsItemSelected(item)
    }
}
