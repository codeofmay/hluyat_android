package com.project.mt.dc.fcm


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.support.v7.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.mt.dc.R
import com.project.mt.dc.charity.activity.CharityNotiActivity
import com.project.mt.dc.donor.activity.DonorNotiActivity
import com.project.mt.dc.donor.activity.LoginActivity
import me.myatminsoe.mdetect.MDetect
import me.myatminsoe.mdetect.Rabbit


/**
 * Created by mt on 7/26/17.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    val currentUser=FirebaseAuth.getInstance().currentUser
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage != null) {
            sendNotification(remoteMessage)
        }
    }

    fun sendNotification(remoteMessage: RemoteMessage) {

        var intent :Intent?= null
        if(remoteMessage.notification.tag =="donor") {
            intent=Intent(this, DonorNotiActivity::class.java)
            intent.putExtra("caller","noti")
        }
        if(remoteMessage.notification.tag =="charity") {
            intent=Intent(this, CharityNotiActivity::class.java)
            intent.putExtra("caller","noti")
        }
        if(currentUser == null){
            intent=Intent(this, LoginActivity::class.java)
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        }
        val pendingIntent= PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        MDetect.init(this)
        val fontFlag=MDetect.isUnicode()
        var title=remoteMessage.notification.title.toString()
        var body= remoteMessage.notification.body.toString()

        if(!fontFlag){
            title=Rabbit.uni2zg(title)
            body=Rabbit.uni2zg(body)
        }

        val bitmap = getBitmapFromURL(remoteMessage.notification.icon!!)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.img)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

    }


    fun getBitmapFromURL(src: String): Bitmap? {
        return Glide.with(this).load(src).asBitmap().centerCrop().into(100,100).get()
    }


}


