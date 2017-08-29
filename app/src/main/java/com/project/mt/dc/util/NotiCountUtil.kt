package com.project.mt.dc.util

import android.content.Context
import android.graphics.drawable.LayerDrawable
import com.project.mt.dc.R


/**
 * Created by mt on 8/13/17.
 */
class NotiCountUtil {
    fun setBadgeCount(context: Context, icon: LayerDrawable, count: Int) {

        val badge: BadgeDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_badge)
        if (reuse != null && reuse is BadgeDrawable) {
            badge = reuse
        } else {
            badge = BadgeDrawable(context)
        }

        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_badge, badge)
    }


}