package com.project.mt.dc.donor.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.project.mt.dc.R
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit

/**
 * Created by mt on 8/25/17.
 */
class BioDialogFragment(donorId:String) : DialogFragment() {
    lateinit var txt_bio:EditText
    val firebaseDB= FirebaseDatabase.getInstance()
    lateinit var progressDialog:ProgressDialog
    var fontFlag: Boolean? = null
    lateinit var lab_ok:TextView
    lateinit var lab_close:TextView
    var donorId=donorId


    override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        MDetect.init(activity)
        fontFlag = MDetect.isUnicode()
        val bioDialog = Dialog(activity!!)

        progressDialog= ProgressDialog(activity)
        progressDialog.setMessage("Please Wait")
        bioDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        bioDialog.setContentView(R.layout.dialog_bio)

        val params = bioDialog.window.attributes

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        bioDialog.window.attributes = params


        txt_bio = bioDialog.findViewById(R.id.txt_bio) as EditText
        lab_ok=bioDialog.findViewById(R.id.lab_ok)as TextView
        lab_close=bioDialog.findViewById(R.id.lab_close)as TextView

        lab_ok.setOnClickListener({
            progressDialog.show()
            var donor_bio=txt_bio.text.toString()
            if(!fontFlag!!){
                donor_bio= Rabbit.zg2uni(donor_bio)
            }
            firebaseDB.getReference("donor").child(donorId)
                    .child("donor_bio")
                    .setValue(donor_bio)
            progressDialog.dismiss()
            bioDialog.dismiss()

        })
        lab_close.setOnClickListener({
            bioDialog.dismiss()
        })
        return bioDialog
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }
}
