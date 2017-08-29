package com.project.mt.dc.charity.activity


import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.R
import com.project.mt.dc.charity.adapter.RecyclerSearchAdapter
import com.project.mt.dc.donor.fragment.BottonSheetFragment
import com.project.mt.dc.event.Event
import com.project.mt.dc.model.DonorInfoModel
import com.project.mt.dc.model.ItemInfoModel
import com.project.mt.dc.model.NotificationModel
import com.project.mt.dc.model.RequestModel
import com.project.mt.dc.service.FirebaseReadService
import com.project.mt.dc.service.FirebaseWriteService
import com.project.mt.dc.service.ShowAddressDialogService
import com.project.mt.dc.util.FontUtil
import com.project.mt.dc.util.MethodUtil
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class CharitySearchActivity : AppCompatActivity() {

    var recyclerSearchList: RecyclerView? = null
    var adapter: RecyclerSearchAdapter? = null
    var township: String? = null
    var category: String? = null
    var lab_category: TextView? = null
    var lab_region: TextView? = null
    var swiperefresh: SwipeRefreshLayout? = null
    var searchList=ArrayList<ItemInfoModel>()
    var fontFlag:Boolean?= null
    lateinit var lab_nodonor:TextView


    lateinit var bottomSheetDialogFragment: BottonSheetFragment
    var donationList: ArrayList<RequestModel>? = null

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser!!.uid
    val donorReference=FirebaseDatabase.getInstance().getReference("donor")

    //request dialog
    var itemModel: ItemInfoModel? = null

    fun search() {

        var region= lab_region!!.text.toString()

        var category= lab_category!!.text.toString()
        if(fontFlag!!){

        }
        else{
            region=Rabbit.zg2uni(region)
            category=Rabbit.zg2uni(category)
        }
        FirebaseReadService().Search(region, category)
    }

    fun search(query: String) {

        val filteredSearchList = ArrayList<ItemInfoModel>()
        for (itemModel: ItemInfoModel in searchList) {
            if (itemModel.item_category!!.toLowerCase().contains(query)
                    ||itemModel.item_amount!!.toLowerCase().contains(query)
                    || itemModel.donor_model!!.donor_name!!.toLowerCase().contains(query)
                    || itemModel.donor_model!!.donor_township!!.toLowerCase().contains(query)
                    || itemModel.donor_model!!.donor_city!!.toLowerCase().contains(query)) {
                filteredSearchList.add(itemModel)
            }
        }
        if (filteredSearchList.size==0){
            lab_nodonor.visibility=View.VISIBLE
        }
        else{
            lab_nodonor.visibility=View.GONE
        }
        adapter= RecyclerSearchAdapter(filteredSearchList,this)
        recyclerSearchList!!.adapter = adapter
        adapter!!.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        FirebaseReadService().getCharityRequest(currentUser)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charity_search)

        FirebaseReadService().getAllDonateItems("search")

        val fontUtil=FontUtil(this)
        var showAddressDialogService= ShowAddressDialogService("search",this)
        showAddressDialogService.setData()
        MDetect.init(this)
        fontFlag=MDetect.isUnicode()

        val toolbar = findViewById(R.id.toolbar_search) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)

        recyclerSearchList = findViewById(R.id.recycler_searchList) as RecyclerView
        swiperefresh = findViewById(R.id.swiperefresh_charitysearch) as SwipeRefreshLayout
        lab_region = findViewById(R.id.lab_searchregion) as TextView
        lab_category = findViewById(R.id.lab_searchcategory) as TextView
        lab_nodonor = findViewById(R.id.lab_nodonor) as TextView
        val img_category = findViewById(R.id.img_searchcategory) as ImageView
        val img_region=findViewById(R.id.img_searchregion) as ImageView

        lab_region!!.typeface=fontUtil.regular_font
        lab_category!!.typeface=fontUtil.regular_font

        val l = android.support.v7.widget.LinearLayoutManager(this)
        recyclerSearchList!!.layoutManager = l

        val dividerItemDecoration = DividerItemDecoration(recyclerSearchList!!.context,
                l.orientation)
        recyclerSearchList!!.addItemDecoration(dividerItemDecoration)

        recyclerSearchList!!.hasFixedSize()
        recyclerSearchList!!.setItemViewCacheSize(20)
        recyclerSearchList!!.isDrawingCacheEnabled = true
        recyclerSearchList!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        var searchHint=getString(R.string.charity_search)

        val searchView = findViewById(R.id.searchview_charity) as SearchView
        searchView.isIconified = false
        if(fontFlag!!){
            searchView.queryHint=searchHint
        }
        else{
            searchView.queryHint=Rabbit.uni2zg(searchHint)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {

                    if (fontFlag!!) {
                        search(newText.toLowerCase())
                    }
                    else{
                        search(Rabbit.zg2uni(newText.toLowerCase()))
                    }

                }
                return true
            }

        })

        swiperefresh!!.setOnRefreshListener {
            FirebaseReadService().Search(lab_region!!.text.toString(), lab_category!!.text.toString())
            swiperefresh!!.isRefreshing = false
        }

        lab_region!!.setOnClickListener({
            showAddressDialogService.setCityDialog()
        })

        lab_category!!.setOnClickListener({
            com.project.mt.dc.service.ShowCategoryDialogService(this, "search")

        })

        img_region!!.setOnClickListener({
            showAddressDialogService.setCityDialog()
        })

        img_category!!.setOnClickListener({
            com.project.mt.dc.service.ShowCategoryDialogService(this, "search")

        })


    }


    @Subscribe
    fun getAllItem(searchListEvent: Event.SearchListEvent) {

        searchList= MethodUtil().reverse(searchListEvent.list!!)
        if (searchList.size==0){
            lab_nodonor.visibility=View.VISIBLE
        }
        else{
            lab_nodonor.visibility=View.GONE
        }

        for(itemModel: ItemInfoModel in searchList){
            donorReference.child(itemModel.donor_id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(datasnapshot: DataSnapshot?) {
                    val donorModel: DonorInfoModel = datasnapshot!!.getValue(DonorInfoModel::class.java)
                    itemModel.donor_model = donorModel
                    adapter!!.notifyDataSetChanged()
                }
            })

        }

        adapter = RecyclerSearchAdapter(searchList, this)
        adapter!!.notifyDataSetChanged()
        adapter!!.setRequsestListener(object : RecyclerSearchAdapter.requestListener {
            override fun getrequestData(itemInfoModel: ItemInfoModel) {
                itemModel = itemInfoModel
                showButtomsheet()

            }

        })
        recyclerSearchList!!.adapter = adapter


    }


    fun showButtomsheet() {

        bottomSheetDialogFragment = BottonSheetFragment(this, donationList!!, null, "charity")
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment!!.tag)

    }

    @Subscribe
    fun getAddress(searchaddressEvent: Event.SearchAddressEvent) {
        township = searchaddressEvent.township
        lab_region!!.text = township
        search()

    }

    @Subscribe
    fun getChosenAddress(addressEvent: Event.AddressEvent) {
        township = addressEvent.township
        lab_region!!.text = township
        search()
    }

    @Subscribe
    fun getChosenCategory(searchCategoryEvent: Event.SearchCategoryEvent) {
        val category = searchCategoryEvent.category
        lab_category!!.text = category
        search()
    }

    @Subscribe
    fun getCharityRequest(listEvent: Event.ListEvent<RequestModel>) {
        if (listEvent.type == "requestlist") {
            donationList = listEvent.list
        }
    }

    @Subscribe
    fun getChosenRequest(requestModelEvent: Event.RequestModelEvent) {

        if(requestModelEvent.caller=="gridcharityrequest") {
            val requestModel = requestModelEvent.requestModel
            val requestDialog=android.support.v7.app.AlertDialog.Builder(this,R.style.MyDialogTheme)
                    .setTitle("Confirmation")
                    .setIcon(android.R.drawable.ic_dialog_info)

            var requestPlace: String?

            if (requestModel != null) {
                if (fontFlag!!) {
                    requestPlace = requestModel.request_place
                } else {
                    requestPlace = Rabbit.uni2zg(requestModel.request_place)
                }

                FirebaseDatabase.getInstance().getReference("donor").child(itemModel!!.donor_id)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(donorSnapshot: DataSnapshot?) {
                                if (donorSnapshot != null) {
                                    var donorName:String?=null
                                    if(fontFlag!!){
                                         donorName= donorSnapshot.child("donor_name").value as String?
                                    }
                                    else{
                                       donorName=Rabbit.uni2zg(donorSnapshot.child("donor_name").value as String?)
                                    }

                                    val requestDate=requestModel.request_date
                                    val message= Html.fromHtml("Request <b>$donorName</b> for donation to\n <b>$requestPlace</b> on $requestDate")
                                    requestDialog.setMessage(message)
                                    requestDialog.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, position: Int) {
                                            setNotification(itemModel!!, requestModel)
                                        }

                                    })
                                    bottomSheetDialogFragment.dismiss()
                                    requestDialog.show()
                                }
                            }

                        })
            }
        }

    }

    fun setNotification(itemModel: ItemInfoModel, requestModel: RequestModel) {
        val notiModel = NotificationModel()
        notiModel.charity_id = currentUser
        notiModel.request_id = requestModel.request_id
        notiModel.item_id = itemModel.item_key
        notiModel.item_category = itemModel.item_category
        notiModel.donor_id=itemModel.donor_id
        notiModel.item_amount = itemModel.item_amount
        notiModel.noti_type="request"

        notiModel.noti_duration = MethodUtil().getCurrentTime()
        FirebaseWriteService().setNotification("donor", notiModel)
        Toast.makeText(this, "Requested", android.widget.Toast.LENGTH_SHORT).show()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {

            if (item.itemId == android.R.id.home) {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
