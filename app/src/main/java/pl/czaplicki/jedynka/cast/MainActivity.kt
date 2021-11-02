package pl.czaplicki.jedynka.cast

import android.graphics.Color
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.*
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import pl.czaplicki.jedynka.cast.databinding.ActivitySlideshowBinding
import java.io.File
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var pageViewPager: ViewPager2
    var parcelFileDescriptor: ParcelFileDescriptor? = null
    var pdfAdapter: PDFAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivitySlideshowBinding.inflate(layoutInflater)
        setContentView(b.root)
        val dotsIndicator = b.dotsIndicator
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
        pageViewPager = b.pageViewPager
        with(pageViewPager) {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
        }
        initPdfViewer(getFile())
        if (pageViewPager.adapter?.itemCount ?: 0 > 1) {
            dotsIndicator.isVisible = true
            dotsIndicator.setViewPager2(pageViewPager)
        } else {
            val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 0)
            b.layout.layoutParams = params
        }


        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                pageViewPager.post(Runnable {
                    pageViewPager.currentItem = (pageViewPager.currentItem + 1) % pageViewPager.adapter?.itemCount!!
                })
            }
        }
        var timer: Timer? = null
        timer = Timer()
        timer.schedule(timerTask, 10000, 10000)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun initPdfViewer(pdfFile: File) {
        try {


            pageViewPager.visibility = View.VISIBLE

            parcelFileDescriptor =
                ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfAdapter = PDFAdapter(parcelFileDescriptor!!, this@MainActivity)
            pageViewPager.adapter = pdfAdapter


        } catch (e: Exception) {
            pdfFile.delete()
        }
    }

    private fun getFile(): File {
        val inputStream = assets.open("example.pdf")
        return File(filesDir.absolutePath + "example.pdf").apply {
            copyInputStreamToFile(inputStream)
        }
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                pageViewPager.post(Runnable {
                    pageViewPager.currentItem =
                        if (pageViewPager.currentItem != 0) pageViewPager.currentItem - 1 else pageViewPager.adapter?.itemCount!!
                })
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                pageViewPager.post(Runnable {
                    pageViewPager.currentItem =
                        (pageViewPager.currentItem + 1) % pageViewPager.adapter?.itemCount!!
                })
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                pageViewPager.post(Runnable { pageViewPager.currentItem = 0 })
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                pageViewPager.post(Runnable {
                    pageViewPager.currentItem = pageViewPager.adapter?.itemCount!!
                })
                true
            }
            else -> super.onKeyUp(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        parcelFileDescriptor?.close()
        pdfAdapter?.clear()

    }
}