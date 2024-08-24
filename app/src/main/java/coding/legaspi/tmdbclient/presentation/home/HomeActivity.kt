package coding.legaspi.tmdbclient.presentation.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.addEvents
import coding.legaspi.tmdbclient.data.model.addevents.AddEvent
import coding.legaspi.tmdbclient.databinding.ActivityHomeBinding
import coding.legaspi.tmdbclient.presentation.about.AboutActivity
import coding.legaspi.tmdbclient.presentation.addevent.AddEventActivity
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.events.EventsActivity
import coding.legaspi.tmdbclient.presentation.home.adapter.HomeAdapter
import coding.legaspi.tmdbclient.presentation.menu.MenuActivity
import coding.legaspi.tmdbclient.presentation.users.UsersActivity
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import java.util.UUID
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: ActivityHomeBinding
    private var rvisLoaded: Boolean = false
    private lateinit var eventList: ArrayList<AddEvent>
    private lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .injectHomeActivity(this)
        eventViewModel=ViewModelProvider(this, factory).get(EventViewModel::class.java)

        eventList = arrayListOf()
        adapter = HomeAdapter(this, eventList, this, eventViewModel)
        binding.loggedInTopNav.labelTitle.text = getString(R.string.dash)
        try {
            binding.progressBar.visibility = VISIBLE
            initRv()
            setBottomButton()
            setMenu()
        }catch (e: Exception){
            Toast.makeText(this, "Check your internet connection...", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStart() {
        super.onStart()
        initRv()
    }

    override fun onResume() {
        super.onResume()
        initRv()

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
                    binding.rvEvents.isNestedScrollingEnabled = false
                    adapter.notifyDataSetChanged()
                    binding.progressBar.visibility = GONE
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
        binding.loggedInBottomNav.events.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }
        binding.loggedInBottomNav.users.setOnClickListener {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
        }
        binding.loggedInBottomNav.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRv() {
        if (rvisLoaded) return
        val llm = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.epoxyRvAdd.layoutManager = llm
        rvisLoaded = true
        val responseLiveData = eventViewModel.getAddEvents()
        responseLiveData.observe(this, Observer {
            if (it!=null){
                binding.epoxyRvAdd.isNestedScrollingEnabled = false
                binding.epoxyRvAdd.withModels {
                    it.forEach {
                        if (it.name != "Cavite City Hymn")
                        addEvents{
                            id(UUID.randomUUID().toString())
                            title(it.name)
                            onClickAddEvent { _ ->
                                val intent = Intent(this@HomeActivity, AddEventActivity::class.java)
                                intent.putExtra("id", it.id)
                                intent.putExtra("name", it.name)
                                startActivity(intent)
                            }
                            initRvForEvents()
                        }
                    }
                }
                binding.epoxyRvAdd.requestModelBuild()
            }else{
                Log.d("Home", "Null")
            }
        })
    }

}