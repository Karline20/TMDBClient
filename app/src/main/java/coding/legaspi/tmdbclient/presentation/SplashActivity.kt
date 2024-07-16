package coding.legaspi.tmdbclient.presentation

import R2
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharePreferences = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // If not, request the permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE)
        } else {
            Handler().postDelayed({
                checkLogin()
                finish()
            }, SPLASH_DELAY)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Handler().postDelayed({
                        checkLogin()
                        finish()
                    }, SPLASH_DELAY)
                } else {
                    Handler().postDelayed({
                        checkLogin()
                        finish()
                    }, SPLASH_DELAY)
                }
                return
            }
        }
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