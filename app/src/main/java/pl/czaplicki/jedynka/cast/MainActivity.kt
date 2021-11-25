package pl.czaplicki.jedynka.cast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import pl.czaplicki.jedynka.cast.formats.pdf.PDFActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.packageName))
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    startActivity(
                        Intent(
                            this,
                            PDFActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }
            requestPermissionLauncher.launch(intent)
        } else {
            startActivity(
                Intent(this, PDFActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Objects.equals(intent.action, Intent.ACTION_BOOT_COMPLETED)) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(i)
        }
    }
}