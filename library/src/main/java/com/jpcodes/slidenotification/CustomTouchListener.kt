package com.jpcodes.slidenotification

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

/**
 * Created by James on 4/24/2019.
 * Detects left and right swipes across a view.
 **/

const val SWIPE_DISTANCE_THRESHOLD = 100
const val SWIPE_VELOCITY_THRESHOLD = 100

/** Movement threshold to cancel a long press timer after ACTION_DOWN press **/
const val LONG_PRESS_MOVEMENT_CANCEL_THRESHOLD = 100

open class OnSwipeTouchListener(context: Context) : OnTouchListener {

  private val gestureDetector: GestureDetector

  private var longPress = false

  private var deltaX = 0.0f
  private var deltaY = 0.0f

  private var p1: Point = Point(0, 0)
  private var p2: Point = Point(0, 0)

  init {
    gestureDetector = GestureDetector(context, GestureListener())
    gestureDetector.setIsLongpressEnabled(false)
  }

  private val longPressRunnable = Runnable {
    longPress = true
  }

  private val longPressHandler = Handler()

  open fun onSwipeLeft() {}

  open fun onSwipeRight() {}

  open fun onSingleTapUp() {}

  override fun onTouch(
    v: View,
    event: MotionEvent
  ): Boolean {

    when(event.action) {

      MotionEvent.ACTION_DOWN -> {
        deltaX = v.x - event.rawX
        deltaY = v.y - event.rawY

        p1 = Point(event.x.toInt(), event.y.toInt())

        // Start a long press timer
        longPressHandler.postDelayed(longPressRunnable, 500)
      }

      MotionEvent.ACTION_MOVE -> {
        p2 = Point(event.x.toInt(), event.y.toInt())

        val movementAmount =
          Math.sqrt(Math.pow(p1.x.toDouble() - p2.x, 2.0) + Math.pow(p1.y.toDouble() - p2.y, 2.0))

        if (longPress) { // drag mode
          v.y = event.rawY + deltaY // update position to the most recent
          //v.x = event.rawX + deltaX
        } else if ((movementAmount > LONG_PRESS_MOVEMENT_CANCEL_THRESHOLD || movementAmount < -LONG_PRESS_MOVEMENT_CANCEL_THRESHOLD) && !longPress) {
          longPressHandler.removeCallbacksAndMessages(null) // cancel long press timer
          longPress = false
        }
      }

      MotionEvent.ACTION_UP -> {
        longPressHandler.removeCallbacksAndMessages(null)
        longPress = false
      }

    }

    // Send to GestureDetector after our custom logic for consuming flings, etc..
    if (gestureDetector.onTouchEvent(event)) {
      //Fling or other gesture
      return true
    }

    return false
  }

  private inner class GestureListener : SimpleOnGestureListener() {

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
      onSingleTapUp()
      return true
    }

    override fun onFling(
      e1: MotionEvent,
      e2: MotionEvent,
      velocityX: Float,
      velocityY: Float
    ): Boolean {
      val distanceX = e2.x - e1.x
      val distanceY = e2.y - e1.y
      if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(
              distanceX
          ) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
      ) {
        if (distanceX > 0)
          onSwipeRight()
        else
          onSwipeLeft()
        return true
      }
      return false
    }

  }
}