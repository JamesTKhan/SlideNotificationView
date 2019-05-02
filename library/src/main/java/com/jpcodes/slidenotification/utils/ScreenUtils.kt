package com.jpcodes.slidenotification.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import com.jpcodes.slidenotification.R

/**
 * Created by James Pooley on 5/2/2019.
 **/
object ScreenUtils {


  /**
   * Returns screen height in pixels. Requires Activity [Context]
   */
  fun getScreenHeight(context: Context): Int {
    //TODO: What if it is a fragment?
    context as Activity
    val display = context.windowManager.defaultDisplay

    val size = Point()
    display.getSize(size)
    //screenWidth = size.x
    val screenHeight = size.y
    //Log.e("SlideLog", "Width $screenWidth")
    Log.e("SlideLog", "height $screenHeight")

    return screenHeight
  }

  /**
   * Returns height of tool bar
   */
  fun getToolBarHeight(context: Context): Int {
    val attrs = intArrayOf(R.attr.actionBarSize)
    val ta = context.obtainStyledAttributes(attrs)
    val toolBarHeight = ta.getDimensionPixelSize(0, -1)
    ta.recycle()
    return toolBarHeight
  }

  /**
   * Returns height of status bar
   */
  fun getStatusBarHeight(context: Context): Int {
    val resources = context.resources
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0)
      resources.getDimensionPixelSize(resourceId)
    else
      Math.ceil(
          ((if (VERSION.SDK_INT >= VERSION_CODES.M) 24 else 25) * resources.displayMetrics.density).toDouble()
      ).toInt()
  }

}