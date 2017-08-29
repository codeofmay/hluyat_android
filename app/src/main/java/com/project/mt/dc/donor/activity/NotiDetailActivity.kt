package com.project.mt.dc.donor.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.mt.dc.MainDonationActivity
import com.project.mt.dc.R
import com.project.mt.dc.charity.activity.CharityProfileActivity
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.service.FirebaseRemoveService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.util.MethodUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class NotiDetailActivity : AppCompatActivity() {
    var lab_charityName: TextView?= null
    var lab_requestPlace: TextView?= null
    var lab_requestDate: TextView?= null
    var lab_requestDescription: TextView?= null
    var img_charityImage: ImageView?= null
    var img_requestImage: ImageView?= null
    var lab_notistatus: TextView?= null
    var img_notistatus: ImageView?= null
    var lab_requestLocation:TextView?= null
    var fontFlag:Boolean?= null
    var btn_accept: Button?= null
    var btn_deny: Button?= null
    var requestModel = RequestModel()
    var donorModel= DonorInfoModel()
    var mFirebaseAuth=FirebaseAuth.getInstance()
    var itemNotiModel: NotificationModel?= null
    var charityNotiModel =NotificationModel()
    val firebaseAuth= FirebaseAuth.getInstance()
    val currentUser= firebaseAuth.currentUser!!.uid
    lateinit var linear_place:LinearLayout
    val firebaseWriteService=FirebaseWriteService()
    val firebaseRemoveService=FirebaseRemoveService()



    fun onClickAccept(v: View){
        //FirebaseReadService().getDonor(mFirebaseAuth.currentUser!!.uid)
        charityNotiModel!!.donor_id= mFirebaseAuth.currentUser!!.uid
        charityNotiModel.charity_id= itemNotiModel!!.from
        charityNotiModel.noti_id= itemNotiModel!!.noti_id
        charityNotiModel.request_id= itemNotiModel!!.request_id
        charityNotiModel!!.item_id= itemNotiModel!!.item_id
        charityNotiModel!!.item_category= itemNotiModel!!.item_category
        charityNotiModel!!.item_amount= itemNotiModel!!.item_amount
        charityNotiModel!!.noti_duration= MethodUtil().getCurrentTime()


        val itemId=itemNotiModel!!.item_id

        if (itemId != null) {
            firebaseWriteService.setDonorList(itemNotiModel!!.from!!,charityNotiModel)

        }



        this.finish()
    }

    fun onClickCancel(v: View){
        android.support.v7.app.AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle("Comfirmation")
                .setMessage("Do you really want to delete this request?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener {
                    dialog, whichButton ->
                    firebaseRemoveService.removeListItem("notification","to_donor", itemNotiModel!!.noti_id!!)
                    Toast.makeText(this,"Deleted",Toast.LENGTH_LONG).show()
                    this.finish()
                })
                .setNegativeButton(android.R.string.no, null).show()

    }


    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_noti_detail)
        val toolbar = findViewById(R.id.toolbar_notiDetail) as Toolbar
        setSupportActionBar(toolbar)
        title="Detail Noti"

        MDetect.init(this)
        fontFlag=MDetect.isUnicode()
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        lab_charityName=findViewById(R.id.lab_charityname)as TextView
        lab_requestPlace=findViewById(R.id.lab_requestplace)as TextView
        lab_requestLocation=findViewById(R.id.lab_requestlocation)as TextView
        lab_requestDate=findViewById(R.id.lab_requestdate)as TextView
        lab_requestDescription=findViewById(R.id.lab_requestdescription)as TextView
        img_charityImage=findViewById(R.id.img_charityimage)as ImageView
        img_requestImage=findViewById(R.id.img_requesimage)as ImageView
        btn_accept=findViewById(R.id.btn_accept)as Button
        btn_deny=findViewById(R.id.btn_deny)as Button
        linear_place=findViewById(R.id.linear_place)as LinearLayout

        val lab_itemAmount=findViewById(R.id.lab_itemamount)as TextView
        val lab_itemCategory=findViewById(R.id.lab_itemcategory)as TextView
        img_notistatus=findViewById(R.id.img_notistatus)as ImageView
        lab_notistatus=findViewById(R.id.lab_notistatus)as TextView

        itemNotiModel= intent.getSerializableExtra("itemNotiModel") as NotificationModel

        Glide.with(this)
                .load(itemNotiModel!!.charity_model!!.charity_image)
                .placeholder(R.drawable.ic_person_black_24dp)
                .bitmapTransform(CropCircleTransformation(this))
                .into(img_charityImage)

        if(fontFlag!!) {
            lab_charityName!!.text = itemNotiModel!!.charity_model!!.charity_name
            lab_itemAmount.text= itemNotiModel!!.item_amount
            lab_itemCategory.text= itemNotiModel!!.item_category
        }
        else{
            lab_charityName!!.text = Rabbit.uni2zg(itemNotiModel!!.charity_model!!.charity_name)
            lab_itemAmount.text= Rabbit.uni2zg(itemNotiModel!!.item_amount)
            lab_itemCategory.text= Rabbit.uni2zg(itemNotiModel!!.item_category)
        }

        val notiStatus= itemNotiModel!!.noti_status


        if(notiStatus == "denied"){
            img_notistatus!!.setImageResource(R.drawable.ic_deny)
            lab_notistatus!!.text="The item had been accepted to donate to other!"

            btn_deny!!.isClickable=false
            btn_accept!!.isClickable=false

            btn_accept!!.isEnabled=false
            btn_deny!!.isEnabled=false
        }
        else if(notiStatus == "accepted"){
            img_notistatus!!.setImageResource(R.drawable.ic_accepted)
            lab_notistatus!!.text="The item had been accepted!"
            btn_accept!!.isEnabled=false
            btn_deny!!.isEnabled=false
            btn_deny!!.isClickable=false
            btn_accept!!.isClickable=false
        }


        lab_charityName!!.setOnClickListener({
            val i= Intent(this, CharityProfileActivity::class.java)
            i.putExtra("caller","visitor")
            i.putExtra("id", requestModel.charity_id)
            startActivity(i)
        })

        linear_place.setOnClickListener({

            val i = Intent(this, MainDonationActivity::class.java)
            i.putExtra("donationmodel", requestModel)
            i.putExtra("caller", "donor")
            startActivity(i)
        })

        FirebaseReadService().getCharityRequest(itemNotiModel!!.from!!, itemNotiModel!!.request_id!!,"notidetail")
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)

    }

    @Subscribe
    fun getRequestPost(requestModelEvent: Event.RequestModelEvent){
        if(requestModelEvent.caller =="notidetail") {
            requestModel = requestModelEvent.requestModel!!
            if (fontFlag!!) {
                lab_requestPlace!!.text = requestModel.request_place
                lab_requestLocation!!.text = requestModel.request_location
                lab_requestDescription!!.text = requestModel.request_description
            }
            else{
                lab_requestPlace!!.text = Rabbit.uni2zg(requestModel.request_place)
                lab_requestLocation!!.text = Rabbit.uni2zg(requestModel.request_location)
                lab_requestDescription!!.text = Rabbit.uni2zg(requestModel.request_description)
            }
            lab_requestDate!!.text = requestModel.request_date
            Glide.with(this).
                    load(requestModel.request_image).
                    into(img_requestImage)
        }
    }


   @Subscribe
    fun getDonor(donorModelEvent: Event.DonorModelEvent) {

       Toast.makeText(this,"Accepted", Toast.LENGTH_SHORT).show()

       //prepare notification for charity onClick accept
       donorModel = donorModelEvent.donorModel!!

   }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)

    }
}
