package com.jpcodes.slidenotification

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by James on 4/24/2019.
 **/
class SlideNotificationView : FrameLayout {

  /** Holds main Layout [View] **/
  private var mainLayout: View

  private var notificationTextView: TextView? = null

  private var notificationImageView: ImageView? = null

  /**
   *   Offset for not closing notification all the way. Gets calculated in onLayout
   */
  private var slideClosedOffset = 0f

  /**
   *   Is the notification collapsed (only semi-visible showing icon)
   */
  private var notificationCollapsed: Boolean = false

  /**
   *  Is the notification visible of off screen
   */
  private var notificationVisible: Boolean = true

  /**
   *  If true, once opened, the view will auto collapse after a timer completes
   */
  private var autoCollapseEnabled = true

  private var autoCollapseTime = 1000 * 5L

  private var slideAnimationDuration = 250L

  /**
   *  Flag for first initial load of the UI
   */
  private var firstLoad = true

  /**
   * Holds a reference to the handler so post delay can be canceled when needed
   */
  private var autoCloseHandler = Handler()

  /**
   * Is the left side layout enabled
   */
  private var leftLayoutEnabled = false

  /**
   * Notification text passed from XML
   */
  private var notificationText: String? = null

  /**
   * Notification text color reference passed from XML
   */
  private var notificationTextColorReference: Int? = null

  /**
   * Notification Icon reference passed from XML
   */
  private var notificationIconReference: Int? = null

  // Chained constructors
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
      context, attrs,
      defStyleAttr
  ) {

    // Get custom attributes
    attrs.let {

      val ta = context.obtainStyledAttributes(attrs, R.styleable.SlideNotificationView, 0, 0)

      ta?.let { typedArray ->
        try {
          leftLayoutEnabled = typedArray.getBoolean(
              R.styleable.SlideNotificationView_slideNotification_enableLeftSideLayout,
              leftLayoutEnabled
          )

          autoCollapseEnabled = typedArray.getBoolean(
              R.styleable.SlideNotificationView_slideNotification_enableAutoCollapse,
              autoCollapseEnabled
          )

          notificationText = typedArray.getString(
              R.styleable.SlideNotificationView_slideNotification_notificationText
          )

          notificationTextColorReference = typedArray.getColor(
              R.styleable.SlideNotificationView_slideNotification_notificationTextColor,
              getColor(R.color.colorDeepPurpleMaterial500)
          )

          notificationIconReference = typedArray.getResourceId(
              R.styleable.SlideNotificationView_slideNotification_notificationIcon,
              R.drawable.ic_message_purple_24dp
          )


          Log.d("SlideLog", "Left Enabled: $leftLayoutEnabled")
          Log.d("SlideLog", "Auto Collapse Enabled: $autoCollapseEnabled")

        } finally {
          ta.recycle()
        }
      }

    }

    mainLayout = if (leftLayoutEnabled)
      View.inflate(context, R.layout.notification_slide_dialog_left, this)
    else
      View.inflate(context, R.layout.notification_slide_dialog, this)

    mainLayout.isClickable = true
    mainLayout.isFocusable = true

    // Get our views
    notificationTextView = findViewById(R.id.slide_notification_text)
    notificationImageView = findViewById(R.id.slide_notification_icon)

    applyAttributesFromXML()
    setListeners()
  }

  private fun applyAttributesFromXML() {
    notificationText?.let {
      if (it.isNotEmpty()) {
        setNotificationText(it)
      }
    }

    notificationIconReference?.let {
      setNotificationIcon(resources.getDrawable(it, null))
    }

    notificationTextColorReference?.let {
      setNotificationTextColor(it)
    }
  }

  /**
   * Start touch listeners
   */
  private fun setListeners() {
    mainLayout.setOnTouchListener(object : OnSwipeTouchListener(context) {
      override fun onSwipeLeft() {
        Log.d("SlideLog", "Swipe left")

        if (leftLayoutEnabled) {
          autoCloseHandler.removeCallbacksAndMessages(null)
          collapse()
        } else
          open()
      }

      override fun onSwipeRight() {
        Log.d("SlideLog", "Swipe right")
        if (leftLayoutEnabled)
          open()
        else {
          autoCloseHandler.removeCallbacksAndMessages(null)
          collapse()
        }
      }

      override fun onSingleTapUp() {
        if (notificationCollapsed)
          open()
        else
        //TODO: Handle notification click
          super.onSingleTapUp()
      }

    })

  }

  override fun onLayout(
    changed: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
  ) {
    super.onLayout(changed, left, top, right, bottom)
    slideClosedOffset = notificationImageView?.width?.toFloat()!!

    // Close the layout when its initially ready
    if (firstLoad) {
      close()
      firstLoad = false
    }

  }

  /**
   * Open (Slide in) the view
   */
  fun open() {
    if (width > 0 && (notificationCollapsed || !notificationVisible)) {

      // Animate open
      ObjectAnimator.ofFloat(this, "translationX", 0f).apply {
        duration = slideAnimationDuration
        start()
      }

      notificationCollapsed = false
      notificationVisible = true

      // Start collapse timer if autoCollapseEnabled enabled and it wasn't manually dragged open
      if (autoCollapseEnabled) {
        startAutoCollapseTimer()
      }
    }
  }

  /**
   * Collapses (slides) the view partially off screen only
   * exposing the icon
   */
  fun collapse() {
    if (width > 0 && !notificationCollapsed) {

      val newTranslation = if (leftLayoutEnabled)
        translationX - width + slideClosedOffset
      else
        translationX + width - slideClosedOffset

      ObjectAnimator.ofFloat(this, "translationX", newTranslation).apply {
        duration = slideAnimationDuration
        start()
      }

      notificationCollapsed = true
    }
  }

  /**
   * Closes the view fully, off screen
   */
  @Suppress("unused") // public method
  fun close() {
    if (width > 0 && notificationVisible) {

      val newTranslation = if (leftLayoutEnabled)
        translationX - width
      else
        translationX + width


      ObjectAnimator.ofFloat(this, "translationX", newTranslation).apply {
        duration = slideAnimationDuration
        start()
      }

      notificationVisible = false
    }
  }

  /**
   * Collapse the view after the timer ends
   */
  private fun startAutoCollapseTimer() {
    autoCloseHandler.postDelayed({
      collapse()
    }, autoCollapseTime)

  }

  /**
   * Sets the text in the UI to the given [text]
   */
  fun setNotificationText(text: String) {
    this.notificationTextView?.text = text
  }

  /**
   * Sets the text color in the UI to the given [resourceId]
   */
  fun setNotificationTextColor(@ColorInt resourceId: Int) {
    this.notificationTextView?.setTextColor(resourceId)
  }

  /**
   * Sets the Icon in the UI to the given [icon]
   */
  fun setNotificationIcon(icon: Drawable) {
    this.notificationImageView?.setImageDrawable(icon)
  }

  /**
   * Set the time to the given [time] to auto collapse the view after opening
   */
  @Suppress("unused")// public method
  fun setAutoCollapseTime(time: Long) {
    this.autoCollapseTime = time
  }

  /**
   * Enable or disable auto collapse feature with the given [value]
   */
  @Suppress("unused") // public method
  fun enableAutoCollapse(value: Boolean) {
    this.autoCollapseEnabled = value
  }

  @Suppress("unused")// public method
  fun isLeftLayoutEnabled(): Boolean {
    return this.leftLayoutEnabled
  }

  /**
   * Utility method to get color safely depending on API level
   */
  private fun getColor(@ColorRes resId: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      resources.getColor(resId, null)
    } else {
      @Suppress("DEPRECATION")
      resources.getColor(resId)
    }

  }

}

