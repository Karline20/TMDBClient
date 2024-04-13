package coding.legaspi.tmdbclient.presentation.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.addevents.AddEvent
import coding.legaspi.tmdbclient.databinding.ActivityEventsBinding
import coding.legaspi.tmdbclient.presentation.about.AboutActivity
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.events.adapter.EventsAdapter
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.home.adapter.HomeAdapter
import coding.legaspi.tmdbclient.presentation.menu.MenuActivity
import coding.legaspi.tmdbclient.presentation.rvevent.RvEventActivity
import coding.legaspi.tmdbclient.presentation.users.UsersActivity
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import javax.inject.Inject

class EventsActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: ActivityEventsBinding
    private lateinit var eventList: ArrayList<AddEvent>
    private lateinit var adapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .injectEventActivity(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        eventList = arrayListOf()
        adapter = EventsAdapter(this, eventList, this, eventViewModel)

        setBottomButton()
        setMenu()
        initRvForEvents()
    }

    override fun onResume() {
        super.onResume()
        initRvForEvents()
    }

    override fun onStart() {
        super.onStart()
        initRvForEvents()
    }

    private fun initRvForEvents() {
        val responseLiveData = eventViewModel.getAddEvents()
        responseLiveData.observe(this, Observer {
            if (it!=null){
                if (it.isEmpty()){
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                }else{
                    eventList.clear()
                    eventList.addAll(it)
                    binding.rvEvents.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    binding.rvEvents.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }else{
                Log.d("Home", "Null")
            }
        })
    }

    private fun setMenu() {
        binding.loggedInTopNav.menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setBottomButton() {
        binding.loggedInBottomNav.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.events.setImageResource(R.drawable.baseline_folder_red)
        binding.loggedInBottomNav.home.setImageResource(R.drawable.outline_home_red)

        binding.loggedInBottomNav.users.setOnClickListener {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}