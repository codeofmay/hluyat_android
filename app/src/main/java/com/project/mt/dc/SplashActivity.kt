package com.project.mt.dc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.mt.dc.charity.activity.CharityNavigationDrawerActivity
import com.project.mt.dc.donor.activity.DonorNavigationDrawerActivity
import com.project.mt.dc.donor.activity.LoginActivity
import io.fabric.sdk.android.Fabric
import me.myatminsoe.mdetect.MDetect


class SplashActivity : Activity() {

    val mFirebaseAuth = FirebaseAuth.getInstance()
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setContentView(R.layout.activity_splash)

        if (mFirebaseAuth.currentUser != null) {

            for (user in FirebaseAuth.getInstance().currentUser!!.providerData) {

                if (user.providerId == "facebook.com") {
                    val userId = mFirebaseAuth.currentUser!!.uid
                    var currentUserReference = FirebaseDatabase.getInstance().getReference("donor")
                    currentUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot?) {
                            if (dataSnapshot!!.child(userId).exists() && dataSnapshot!!.child(userId).child("donor_phone").exists()) {
                                Handler().postDelayed({
                                    val i = Intent(this@SplashActivity, DonorNavigationDrawerActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }, 1000)
                            }

                        }

                    })
                } else if (user.providerId == "password") {
                    Handler().postDelayed({
                        val i = Intent(this@SplashActivity, CharityNavigationDrawerActivity::class.java)
                        startActivity(i)
                        finish()
                    }, 1000)


                }
            }

        } else {

            val i = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(i)
            finish()

        }
    }
}
