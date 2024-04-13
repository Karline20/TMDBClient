package coding.legaspi.tmdbclient.presentation.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.users.Users
import coding.legaspi.tmdbclient.databinding.ActivityUsersBinding
import coding.legaspi.tmdbclient.presentation.about.AboutActivity
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.events.EventsActivity
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.menu.MenuActivity
import coding.legaspi.tmdbclient.presentation.users.adapter.UserAdapter
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import javax.inject.Inject

class UsersActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var usersBinding: ActivityUsersBinding
    private lateinit var adapter: UserAdapter
    private lateinit var usersList: ArrayList<Users>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usersBinding = ActivityUsersBinding.inflate(layoutInflater)
        val view = usersBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .injectUserActivity(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        usersList = arrayListOf()
        adapter = UserAdapter(this, usersList)
        usersBinding.loggedInTopNav.labelTitle.text = "Active Users"
        usersBinding.progressBar.visibility = VISIBLE
        setBottomButton()
        setMenu()
        initRv()
    }

    override fun onStart() {
        super.onStart()
        initRv()
    }

    override fun onResume() {
        super.onResume()
        initRv()
    }

    private fun initRv() {
        val responseLiveData = eventViewModel.getUser()
        responseLiveData.observe(this, Observer {
            if (it!=null){
                if (it.isEmpty()){
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                    usersBinding.noData.visibility = VISIBLE
                    usersBinding.rvUser.visibility = GONE
                    usersBinding.progressBar.visibility = GONE
                }else{
                    usersList.clear()
                    usersList.addAll(it)
                    usersBinding.rvUser.layoutManager = LinearLayoutManager(this)
                    usersBinding.rvUser.adapter = adapter
                    usersBinding.noData.visibility = GONE
                    usersBinding.rvUser.visibility = VISIBLE
                    usersBinding.progressBar.visibility = GONE
                    adapter.notifyDataSetChanged()
                }
            }else{
                Log.d("Home", "Null")
            }
        })
    }

    private fun setMenu() {
        usersBinding.loggedInTopNav.menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun setBottomButton() {
        usersBinding.loggedInBottomNav.users.setImageResource(R.drawable.person_red)
        usersBinding.loggedInBottomNav.home.setImageResource(R.drawable.outline_home_red)

        usersBinding.loggedInBottomNav.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        usersBinding.loggedInBottomNav.events.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
            finish()
        }
        usersBinding.loggedInBottomNav.users.setOnClickListener {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
        usersBinding.loggedInBottomNav.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}