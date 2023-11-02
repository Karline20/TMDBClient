package coding.legaspi.tmdbclient.presentation.menu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coding.legaspi.tmdbclient.BuildConfig
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.databinding.ActivityMenuBinding
import coding.legaspi.tmdbclient.presentation.about.AboutActivity
import coding.legaspi.tmdbclient.presentation.events.EventsActivity
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.login.LoginActivity
import coding.legaspi.tmdbclient.presentation.rvevent.RvEventActivity
import coding.legaspi.tmdbclient.presentation.users.UsersActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var activityMenuBinding: ActivityMenuBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMenuBinding = ActivityMenuBinding.inflate(layoutInflater)
        val view = activityMenuBinding.root
        setContentView(view)
        sharedPreferences = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

        setMenuButton()
    }

    private fun setMenuButton() {
        activityMenuBinding.back.setOnClickListener {
            onBackPressed()
            finish()
        }
        activityMenuBinding.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        activityMenuBinding.events.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
            finish()
        }
        activityMenuBinding.users.setOnClickListener {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
        activityMenuBinding.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            finish()
        }
        activityMenuBinding.logout.setOnClickListener {
            try {
                sharedPreferences.edit().remove("token").apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Can't logout...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}