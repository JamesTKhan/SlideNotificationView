package jpcodes.com.slidenotificationexample

import android.os.Bundle
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
  private lateinit var leftSlideView: SlideNotificationView
  private lateinit var rightSlideView: SlideNotificationView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    mainLayout = findViewById(R.id.main_constraint_layout)
    leftSlideView = findViewById(R.id.left_slide_notification_view)
    rightSlideView = findViewById(R.id.right_slide_notification_view)
    activateLeftButton = findViewById(R.id.activate_left_button)
    activateRightButton = findViewById(R.id.activate_right_button)

    activateLeftButton.setOnClickListener {
      openLeftSlider()
    }

    activateRightButton.setOnClickListener {
      openRightSlider()
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

}
