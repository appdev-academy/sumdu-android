package academy.appdev.sumdu.activities

import academy.appdev.sumdu.MainActivity
import academy.appdev.sumdu.networking.getLists
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLists { navigateNext() }
    }

    private fun navigateNext() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}