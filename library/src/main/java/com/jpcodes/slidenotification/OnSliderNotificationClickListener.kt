package com.jpcodes.slidenotification

/**
 * Created by James Pooley on 5/2/2019.
 *
 * Interface for implementing a callback to be
 * invoked when the slide is tapped while open
 */
interface OnSliderNotificationClickListener {

  /**
   * Called when an open slider notification is tapped
   */
  fun onSliderNotificationClicked()

}