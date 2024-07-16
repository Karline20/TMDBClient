package coding.legaspi.tmdbclient.presentation.rvevent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.eventsoutput.AllModelOutput
import coding.legaspi.tmdbclient.databinding.ActivityRvEventBinding
import coding.legaspi.tmdbclient.presentation.about.AboutActivity
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.menu.MenuActivity
import coding.legaspi.tmdbclient.presentation.users.UsersActivity
import javax.inject.Inject

class RvEventActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: ActivityRvEventBinding

    lateinit var name : String
    private lateinit var eventAdapter: EventAdapter
    private lateinit var allModelOutput: ArrayList<AllModelOutput>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRvEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        name = intent.getStringExtra("name").toString()
        (application as Injector).createEventsSubComponent()
            .injectRvEventActivity(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        allModelOutput = arrayListOf()
        eventAdapter = EventAdapter(allModelOutput, this)

        binding.loggedInTopNav.labelTitle.text = name
        setBottomButton()
        setMenu()
    }

    private fun setCategory(name: String) {
        binding.llHistory.visibility = GONE
        binding.llContacts.visibility = GONE
        binding.llHotres.visibility = GONE
        binding.llFoods.visibility = GONE
        binding.llSchool.visibility = GONE
        when(name){
            "History" -> {
                binding.llHistory.visibility = VISIBLE
                setBindingHistory()
            }
            "Foods" -> {
                binding.llFoods.visibility = VISIBLE
                setBindingFoods()
            }
            "Hotel & Resorts" -> {
                binding.llHotres.visibility = VISIBLE
                setBindingHotel()
            }
            "Emergency Care & Contacts" -> {
                binding.llContacts.visibility = VISIBLE
                setBindingContacts()
            }
            "Schools" -> {
                binding.llSchool.visibility = VISIBLE
                setBindingSchool()
            }
        }
    }

    private fun setBindingSchool() {
        binding.elementary.setOnClickListener {
            setRvCate("Elementary")
        }
        binding.highschool.setOnClickListener {
            setRvCate("Highschool")
        }
        binding.college.setOnClickListener {
            setRvCate("College")
        }
        binding.vocational.setOnClickListener {
            setRvCate("Vocational")
        }
        binding.other.setOnClickListener {
            setRvCate("Other")
        }
    }

    private fun setBindingHistory() {
        binding.heroes.setOnClickListener {
            setRvCate("Heroes")
        }
        binding.historical.setOnClickListener {
            setRvCate("Historical Places")
        }
        binding.events.setOnClickListener {
            setRvCate("Events")
        }
    }

    private fun setBindingHotel() {
        binding.resort.setOnClickListener {
            setRvCate("Resort")
        }
        binding.hotel.setOnClickListener {
            setRvCate("Hotel")
        }
    }

    private fun setBindingContacts() {
        binding.hospital.setOnClickListener {
            setRvCate("Hospital")
        }
        binding.pharmacy.setOnClickListener {
            setRvCate("Pharmacy")
        }
        binding.police.setOnClickListener {
            setRvCate("Police Station")
        }
        binding.fire.setOnClickListener {
            setRvCate("Fire Station")
        }
    }

    private fun setBindingFoods() {
        binding.cafes.setOnClickListener {
            setRvCate("Cafes")
        }
        binding.stores.setOnClickListener {
            setRvCate("Convenience Stores")
        }
        binding.fastfood.setOnClickListener {
            setRvCate("Fast Food")
        }
        binding.homemade.setOnClickListener {
            setRvCate("Homemade Foods")
        }
        binding.restaurant.setOnClickListener {
            setRvCate("Restaurants")
        }
        binding.delicacies.setOnClickListener {
            setRvCate("Famous Delicacies")
        }
    }

    private fun setRvCate(category: String){
        val responseLiveData = eventViewModel.getCategory(category)
        try {
            responseLiveData.observe(this, Observer {
                if (it!=null){
                    if (it.isEmpty()){
                        binding.noData.visibility=VISIBLE
                        binding.epoxyRvAdd.visibility= GONE
                        binding.labelWelcome.text = "No list for $category"
                    }else{
                        allModelOutput.clear()
                        allModelOutput.addAll(it)
                        val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        binding.epoxyRvAdd.layoutManager = llm
                        binding.epoxyRvAdd.adapter = eventAdapter
                        binding.labelWelcome.text = "Category $name"
                        eventAdapter.notifyDataSetChanged()
                        binding.noData.visibility= GONE
                        binding.epoxyRvAdd.visibility= VISIBLE
                    }
                }else{
                    binding.noData.visibility=VISIBLE
                    binding.epoxyRvAdd.visibility= GONE
                    Log.d("RvEvent", "Null")
                }
            })
        }catch (e: Exception){
            binding.noData.visibility=VISIBLE
            binding.epoxyRvAdd.visibility= GONE
        }
    }

    override fun onResume() {
        super.onResume()
        binding.noData.visibility= GONE
        Log.d("RvEvent", name)
        initRv()
        binding.rrMid.visibility = GONE
        when(name){
            "History", "Foods","Hotel & Resorts","Emergency Care & Contacts", "Schools"  -> {
                binding.rrMid.visibility = VISIBLE
                setCategory(name)
            }
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

    override fun onStart() {
        super.onStart()
        binding.noData.visibility= GONE
        Log.d("RvEvent", name)
        initRv()
        binding.rrMid.visibility = GONE
        when(name){
            "History", "Foods","Hotel & Resorts","Emergency Care & Contacts", "Schools"   -> {
                binding.rrMid.visibility = VISIBLE
                setCategory(name)
            }
        }
    }

    private fun initRv() {
        val responseLiveData = eventViewModel.getEventsByCategory(name)
        try {
            responseLiveData.observe(this, Observer {
                if (it!=null){
                    allModelOutput.clear()
                    allModelOutput.addAll(it)
                    val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    binding.epoxyRvAdd.layoutManager = llm
                    binding.epoxyRvAdd.adapter = eventAdapter
                    eventAdapter.notifyDataSetChanged()
                    binding.noData.visibility= GONE
                    binding.epoxyRvAdd.visibility= VISIBLE
                }else{
                    binding.noData.visibility=VISIBLE
                    binding.epoxyRvAdd.visibility= GONE
                    Log.d("RvEvent", "Null")
                }
            })
        }catch (e: Exception){
            binding.noData.visibility=VISIBLE
            binding.epoxyRvAdd.visibility= GONE
        }
    }
}