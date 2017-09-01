package com.project.mt.dc.service

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by mt on 6/23/17.
 */
open class FirebaseService : MultiDexApplication(){
    private var context: Context? = null
    open var firebaseDatabase: FirebaseDatabase? = null

    override fun onCreate() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        (firebaseDatabase)!!.setPersistenceEnabled(true)

        this.context = getApplicationContext()
        super.onCreate()
    }

    fun getContext(): Context? {
        return this.context
    }
}