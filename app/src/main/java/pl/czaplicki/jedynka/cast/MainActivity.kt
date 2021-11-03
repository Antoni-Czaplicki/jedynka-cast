package pl.czaplicki.jedynka.cast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.czaplicki.jedynka.cast.formats.pdf.PDFActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, PDFActivity::class.java))
    }
}