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
        val card_terms=view.findViewById(R.id.card_terms)as CardView
        card_licenses.setOnClickListener({
            val view = LayoutInflater.from(activity).inflate(R.layout.dialog_licenses, null) as WebView
            view.loadUrl("file:///android_asset/opensource_license.html")
            AlertDialog.Builder(activity, R.style.MyDialogTheme)
                    .setTitle("Open Source Licenses")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
        })
        card_terms.setOnClickListener({
            val view = LayoutInflater.from(activity).inflate(R.layout.dialog_licenses, null) as WebView
            view.loadUrl("file:///android_asset/terms_n_conditions.html")
            AlertDialog.Builder(activity, R.style.MyDialogTheme)
                    .setTitle("Terms And Conditions")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
        })
      return view
    }

}// Required empty public constructor
