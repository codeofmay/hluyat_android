package com.project.mt.dc.fcm

import android.preference.PreferenceManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


/**
 * Created by mt on 7/26/17.
 */
class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()


        val instance_id = FirebaseInstanceId.getInstance().token
        if (instance_id != null) {

            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putString("instance_id", instance_id).apply()

        }
    }

}
