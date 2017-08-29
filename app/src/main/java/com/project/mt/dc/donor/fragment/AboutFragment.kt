package com.project.mt.dc.donor.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.project.mt.dc.R
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


class AboutFragment : Fragment() {

    lateinit var lab_about_donate:TextView
    lateinit var lab_about_search:TextView
    lateinit var lab_about_noti:TextView
    lateinit var lab_about_finish:TextView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view=inflater!!.inflate(R.layout.fragment_about, container, false)

        MDetect.init(activity)
        val fontFlag=MDetect.isUnicode()

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
        return view
    }

}// Required empty public constructor
