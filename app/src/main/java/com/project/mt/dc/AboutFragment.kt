package com.project.mt.dc


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.mt.dc.R
import me.myatminsoe.mdetect.MDetect
import android.webkit.WebView




class AboutFragment : Fragment() {

    lateinit var card_licenses:CardView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view=inflater!!.inflate(R.layout.fragment_about, container, false)

        MDetect.init(activity)
        card_licenses=view.findViewById(R.id.card_licenses)as CardView
        card_licenses.setOnClickListener({
            val view = LayoutInflater.from(activity).inflate(R.layout.dialog_licenses, null) as WebView
            view.loadUrl("file:///android_asset/opensource_license.html")
            AlertDialog.Builder(activity, R.style.MyDialogTheme)
                    .setTitle("Open Source Licenses")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
        })
/*

        lab_about_donate=view.findViewById(R.id.lab_about_donate)as TextView
        lab_about_search=view.findViewById(R.id.lab_about_search)as TextView
        lab_about_noti=view.findViewById(R.id.lab_about_noti)as TextView
        lab_about_finish=view.findViewById(R.id.lab_about_finish)as TextView


        if(fontFlag!!){
            lab_about_donate.text=getString(R.string.donor_about_donate)
            lab_about_search.text=getString(R.string.donor_about_search)
            lab_about_noti.text=getString(R.string.donor_about_noti)
            lab_about_finish.text=getString(R.string.donor_about_finish)
        }
            else{

            lab_about_donate.text=Rabbit.uni2zg(getString(R.string.donor_about_donate))
            lab_about_search.text=Rabbit.uni2zg(getString(R.string.donor_about_search))
            lab_about_noti.text=Rabbit.uni2zg(getString(R.string.donor_about_noti))
            lab_about_finish.text=Rabbit.uni2zg(getString(R.string.donor_about_finish))
        }
*/
        return view
    }

}// Required empty public constructor
