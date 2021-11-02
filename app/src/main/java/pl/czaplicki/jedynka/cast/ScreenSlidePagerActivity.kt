package pl.czaplicki.jedynka.cast

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import java.util.*

class ScreenSlidePagerActivity : FragmentActivity() {
    companion object {
        var image_list: MutableList<Int> = mutableListOf()
        fun load_images(url: String) {
            image_list.add(R.drawable.app_icon)
        }
    }

    private lateinit var viewPager: ViewPager2
    var timer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideshow)
        load_images("")
        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.main_browse_fragment)

        // The pager adapter, which provides the pages to the view pager widget.
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                viewPager.post(Runnable {
                    viewPager.currentItem = (viewPager.currentItem + 1) % image_list.size
                })
            }
        }
        timer = Timer()
        timer!!.schedule(timerTask, 10000, 10000)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                viewPager.post(Runnable {
                    viewPager.currentItem = (viewPager.currentItem + 1) % image_list.size
                })
            }
        }
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                viewPager.post(Runnable {
                    viewPager.currentItem =
                        if (viewPager.currentItem != 0) viewPager.currentItem - 1 else image_list.size
                })
                timer?.cancel()
                timer = Timer()
                timer!!.schedule(timerTask, 10000, 10000)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                viewPager.post(Runnable {
                    viewPager.currentItem = (viewPager.currentItem + 1) % image_list.size
                })
                timer?.cancel()
                timer = Timer()
                timer!!.schedule(timerTask, 10000, 10000)
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                viewPager.post(Runnable { viewPager.currentItem = 0 })
                timer?.cancel()
                timer = Timer()
                timer!!.schedule(timerTask, 10000, 10000)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                viewPager.post(Runnable { viewPager.currentItem = image_list.size })
                timer?.cancel()
                timer = Timer()
                timer!!.schedule(timerTask, 10000, 10000)
                true
            }
            else -> super.onKeyUp(keyCode, event)
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
}