package coding.legaspi.tmdbclient.presentation

import R2
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import coding.legaspi.tmdbclient.BuildConfig
import coding.legaspi.tmdbclient.databinding.ActivitySplashBinding
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.login.LoginActivity
import coding.legaspi.tmdbclient.utils.FirebaseManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 1000
    private lateinit var sharePreferences: SharedPreferences
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharePreferences = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

        Handler().postDelayed({
            checkLogin()
            finish()
        }, SPLASH_DELAY)
    }

    private fun checkLogin() {
        Log.d("SplashActivity", "Entering checkLogin()")
        val token = sharePreferences.getString("token", null)
        Log.d("SplashActivity", "Token: $token")
        if (token != null) {
            Log.d("SplashActivity", "Token is not null, MainActivity")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.d("SplashActivity", "Token is null, LoginActivity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}