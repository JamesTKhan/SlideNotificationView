package jpcodes.com.slidenotificationexample

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeLeft
import android.support.test.espresso.action.ViewActions.swipeRight
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NotificationSlideViewInstrumentedTest {

  @Rule
  @JvmField
  val activityTestRule = ActivityTestRule(MainActivity::class.java)

  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getTargetContext()
    assertEquals("jpcodes.com.slidenotificationexample", appContext.packageName)
  }

  @Test
  fun check_slideViewsNotVisibleInitially() {
    onView(withId(R.id.left_slide_notification_view)).check(
        matches(not(isDisplayed()))
    )

    onView(withId(R.id.right_slide_notification_view)).check(
        matches(not(isDisplayed()))
    )
  }

  @Test
  fun check_slideViewsVisibleAfterButtonClick() {
    openLeftSlideView()
    Thread.sleep(500)
    onView(withId(R.id.left_slide_notification_view)).check(matches(isCompletelyDisplayed()))

    openRightSlideView()
    Thread.sleep(500)
    onView(withId(R.id.right_slide_notification_view)).check(matches(isCompletelyDisplayed()))
  }

  @Test
  fun check_slideViewsPartiallyCloseOnSwipeLeft() {
    openLeftSlideView()
    Thread.sleep(500)
    onView(withId(R.id.left_slide_notification_view)).perform(swipeLeft())
    Thread.sleep(500)
    onView(withId(R.id.left_slide_notification_view)).check(matches(not(isCompletelyDisplayed()))) // should be partially hidden at this point
    onView(withId(R.id.slide_notification_icon_left)).check(matches(isDisplayed())) // should stay visible on collapse
  }

  @Test
  fun check_slideViewsPartiallyCloseOnSwipeRight() {
    openRightSlideView()
    Thread.sleep(500)
    onView(withId(R.id.right_slide_notification_view)).perform(swipeRight())
    Thread.sleep(500)
    onView(withId(R.id.right_slide_notification_view)).check(matches(not(isCompletelyDisplayed()))) // should be partially hidden at this point
    onView(withId(R.id.slide_notification_icon)).check(matches(isDisplayed())) // should stay visible on collapse
  }

  @Test
  fun check_leftViewClosesAfterOpenClick() {
    openLeftSlideView()
    Thread.sleep(500)
    clickLeftSlideView()
    Thread.sleep(500)
    onView(withId(R.id.left_slide_notification_view)).check(matches(not(isDisplayed())))
  }

  @Test
  fun check_rightViewClosesAfterOpenClick() {
    openRightSlideView()
    Thread.sleep(500)
    clickRightSlideView()
    Thread.sleep(500)
    onView(withId(R.id.right_slide_notification_view)).check(matches(not(isDisplayed())))
  }

  @Test
  fun check_rightSlideViewSlidesOutOnClick() {
    openRightSlideView()
    Thread.sleep(6500) // waiting for view to auto close
    onView(withId(R.id.slide_notification_icon)).perform(click())
    onView(withId(R.id.right_slide_notification_view)).check(matches(isCompletelyDisplayed()))
  }

  @Test
  fun check_leftSlideViewSlidesOutOnClick() {
    openLeftSlideView()
    Thread.sleep(500)
    onView(withId(R.id.left_slide_notification_view)).perform(swipeLeft())
    Thread.sleep(500)
    onView(withId(R.id.slide_notification_icon_left)).perform(click())
    Thread.sleep(500)
    onView(withId(R.id.left_slide_notification_view)).check(matches(isCompletelyDisplayed()))
  }

  private fun openLeftSlideView() {
    onView((withId(R.id.activate_left_button))).perform(click())
  }

  private fun openRightSlideView() {
    onView((withId(R.id.activate_right_button))).perform(click())
  }

  private fun clickLeftSlideView() {
    onView(withId(R.id.left_slide_notification_view)).perform(click())
  }

  private fun clickRightSlideView() {
    onView(withId(R.id.right_slide_notification_view)).perform(click())
  }

}
