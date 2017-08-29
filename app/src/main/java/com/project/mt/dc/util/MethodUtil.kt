package com.project.mt.dc.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream


class MethodUtil {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
    val changeDateFormat=SimpleDateFormat("dd MMM, yyyy")

    fun <T> reverse(list: ArrayList<T>): ArrayList<T> {
        if (list.size > 1) {
            val value = list.removeAt(0)
            reverse(list)
            list.add(value)
        }
        return list
    }

    fun bitmapToByte(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteArray = stream.toByteArray()
        return byteArray
    }

    fun splitDate(timeStamp: String): String{
        val stringArray = timeStamp.split(" ")
        return stringArray[0]
    }
    fun splitName(Name: String): String{
        val stringArray = Name.split(" ")
        var name:String
        if(stringArray.size==1){
            name=stringArray[0]
        }
        else{
            name=stringArray[0]+" "+stringArray[1]
        }
        return name
    }

    fun getCurrentTime(): String {
        var calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentTime = dateFormat.format(calendar.time)
        return currentTime
    }



    fun getCurrentDate(): String {

        val calendar = Calendar.getInstance()
        val currentDate = dateFormat.format(calendar.time)
        return currentDate
    }


    fun changeDateFormat(date:String): String{
        val originalDate = dateFormat.parse(date)
        val formatDate = changeDateFormat.format(originalDate)
        return formatDate
    }


    fun getTimeDifferent(time: String): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val past = dateFormat.parse(time)
        val now = Date()

        val second = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minute = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hour = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
        val day = TimeUnit.MILLISECONDS.toDays(now.time - past.time)


        if (day > 365) {
            val year = day / 365
            val time = year.toString() + " yr"
            return time
        } else if (day > 30) {
            val month = day / 30
            val time = month.toString() + " month"
            return time
        } else if (day > 0) {
            val time = day.toString() + " day"
            return time
        } else if (hour > 0) {
            val time = hour.toString() + " hr"
            return time
        } else if (minute > 0) {
            val time = minute.toString() + " min"
            return time
        } else {
            val time = second.toString() + " sec"
            return time
        }
        return ""

    }



}
