package coding.legaspi.tmdbclient.presentation.about

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.databinding.ActivityAboutBinding
import coding.legaspi.tmdbclient.presentation.about.aboutus.AboutUsActivity
import coding.legaspi.tmdbclient.presentation.about.researcher.ResearcherActivity
import coding.legaspi.tmdbclient.presentation.about.terms.TermsActivity
import coding.legaspi.tmdbclient.presentation.events.EventsActivity
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.menu.MenuActivity
import coding.legaspi.tmdbclient.presentation.users.UsersActivity

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setBottomButton()
        setMenu()
        setButton()
    }

    private fun setButton() {
        binding.aboutBtn.setOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }
        binding.termsBtn.setOnClickListener {
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
        }
        binding.researcherBtn.setOnClickListener {
            val intent = Intent(this, ResearcherActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setMenu() {
        binding.loggedInTopNav.menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setBottomButton() {
        binding.loggedInBottomNav.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.events.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.users.setOnClickListener {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.info.setImageResource(R.drawable.info_red)
        binding.loggedInBottomNav.home.setImageResource(R.drawable.outline_home_red)

    }

}