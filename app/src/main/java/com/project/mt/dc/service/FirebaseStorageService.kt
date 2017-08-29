package com.project.mt.dc.service

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FirebaseStorage
import com.project.mt.dc.event.Event.RequestModelEvent
import com.project.mt.dc.model.RequestModel
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayOutputStream


/**
 * Created by mt on 6/30/17.
 */
class FirebaseStorageService {

    val firebaseStorage = FirebaseStorage.getInstance()
    fun upload(rootName: String, image_id: String, imageView: ImageView,caller:String) {

        var downloadUrl: Uri? = null

        val storageRef = firebaseStorage.reference.child("$rootName/$image_id.jpg")

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = imageView.drawingCache
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener(OnFailureListener {
            // Handle unsuccessful uploads
        }).addOnSuccessListener({ taskSnapshot ->
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            downloadUrl = taskSnapshot.downloadUrl
            val doneModel = RequestModel()
            doneModel.request_id = image_id
            doneModel.request_image = downloadUrl.toString()
            EventBus.getDefault().post(RequestModelEvent(caller,doneModel))

        })

    }

    fun upload1(rootName: String, image_id: String, imageBitmap: Bitmap,caller:String) {

        var downloadUrl: Uri? = null

        val storageRef = firebaseStorage.reference.child("$rootName/$image_id.jpg")

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener(OnFailureListener {
            // Handle unsuccessful uploads
        }).addOnSuccessListener({ taskSnapshot ->
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            downloadUrl = taskSnapshot.downloadUrl
            val doneModel = RequestModel()
            doneModel.request_id = image_id
            doneModel.request_image = downloadUrl.toString()
            EventBus.getDefault().post(RequestModelEvent(caller,doneModel))

        })

    }
}
