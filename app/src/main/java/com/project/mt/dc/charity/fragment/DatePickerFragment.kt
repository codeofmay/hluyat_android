package com.project.mt.dc.charity.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import android.widget.TextView
import java.util.*


/**
 * Created by mt on 7/24/17.
 */


open class DatePickerFragment(activity: Activity, textView: TextView) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    val activity=activity
    val textView=textView
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val trueYear=year-1900
        val dateFormat = java.text.SimpleDateFormat("dd MMM, yyyy")
        val requestDate: String = dateFormat.format(Date(trueYear, month, dayOfMonth))
        textView.text=requestDate
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity, this, year, month, day)
    }


}