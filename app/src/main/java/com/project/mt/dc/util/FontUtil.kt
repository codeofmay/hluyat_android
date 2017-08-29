package com.project.mt.dc.util

import android.app.Activity
import android.graphics.Typeface

/**
 * Created by mt on 7/16/17.
 */
class FontUtil(activity: Activity) {
    var regular_font: Typeface?= null
    var title_font: Typeface?= null
    var medium_font: Typeface?= null
    var light_font: Typeface?= null
    var italic_font:Typeface?=null
    var open_san_regular:Typeface?= null
    var open_san_bold:Typeface?= null
    val activity=activity

    init {
        regular_font = Typeface.createFromAsset(activity.assets, "fonts/Roboto-Regular.ttf")
        title_font = Typeface.createFromAsset(activity.assets, "fonts/Roboto-Bold.ttf")
        italic_font = Typeface.createFromAsset(activity.assets, "fonts/Roboto-Italic.ttf")
        light_font = Typeface.createFromAsset(activity.assets, "fonts/Roboto-Light.ttf")
        open_san_regular=Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Regular.ttf")
        open_san_bold=Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Bold.ttf")
    }

}