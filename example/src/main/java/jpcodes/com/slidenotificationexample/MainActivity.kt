package jpcodes.com.slidenotificationexample

import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.jpcodes.slidenotification.OnSliderNotificationClickListener
import com.jpcodes.slidenotification.SlideNotificationView

class MainActivity : AppCompatActivity() {

  private lateinit var mainLayout: ConstraintLayout
  private lateinit var activateLeftButton: Button
  private lateinit var activateRightButton: Button
  private lateinit var rightSliderStyleButton: Button
  private lateinit var leftSlideView: SlideNotificationView
  private lateinit var rightSlideView: SlideNotificationView

  private var styleChanged = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    mainLayout = findViewById(R.id.main_constraint_layout)
    leftSlideView = findViewById(R.id.left_slide_notification_view)
    rightSlideView = findViewById(R.id.right_slide_notification_view)
    activateLeftButton = findViewById(R.id.activate_left_button)
    activateRightButton = findViewById(R.id.activate_right_button)
    rightSliderStyleButton = findViewById(R.id.right_style_change)

    activateLeftButton.setOnClickListener {
      openLeftSlider()
    }

    activateRightButton.setOnClickListener {
      openRightSlider()
      rightSliderStyleButton.isEnabled = true
    }

    rightSliderStyleButton.setOnClickListener {
      changeStyleOfRightSlider()
    }

    leftSlideView.setSliderNotificationClickListener(object : OnSliderNotificationClickListener {
      override fun onSliderNotificationClicked() {
        Snackbar.make(mainLayout, "Left Notification Clicked", Snackbar.LENGTH_SHORT)
          .show()
        leftSlideView.close()
      }
    })

    rightSlideView.setSliderNotificationClickListener(object : OnSliderNotificationClickListener {
      override fun onSliderNotificationClicked() {
        Snackbar.make(mainLayout, "Right Notification Clicked", Snackbar.LENGTH_SHORT)
          .show()
        rightSlideView.close()
      }
    })

  }

  private fun openLeftSlider() {
    leftSlideView.open()
  }

  private fun openRightSlider() {
    rightSlideView.setNotificationText(getString(R.string.notification_new_message))
    rightSlideView.setNotificationIcon(getDrawable(R.drawable.ic_message_purple_24dp)!!)
    rightSlideView.open()
  }

  private fun changeStyleOfRightSlider() {
    if (!styleChanged) {
      rightSlideView.setNotificationTextColor(getColorSafe(R.color.colorWhite))
      rightSlideView.setBackgroundColor(getColorSafe(R.color.colorDarkGrey))
      rightSlideView.setNotificationIcon(getDrawable(R.drawable.ic_android_white_24dp)!!)
    } else {
      rightSlideView.setNotificationTextColor(getColorSafe(R.color.colorDeepPurpleMaterial500))
      rightSlideView.setBackgroundColor(getColorSafe(R.color.colorWhite))
      rightSlideView.setNotificationIcon(getDrawable(R.drawable.ic_message_purple_24dp)!!)
    }
    styleChanged = !styleChanged
  }

  /**
   * Utility method to get color safely depending on API level
   */
  private fun getColorSafe(@ColorRes resId: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      resources.getColor(resId, null)
    } else {
      @Suppress("DEPRECATION")
      resources.getColor(resId)
    }

  }

}
