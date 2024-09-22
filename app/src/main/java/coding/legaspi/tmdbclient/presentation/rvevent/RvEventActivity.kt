package coding.legaspi.tmdbclient.presentation.rvevent

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.SearchView
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
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.DialogHelperFactory
import java.io.IOException
import javax.inject.Inject
import coding.legaspi.tmdbclient.Result
import coding.legaspi.tmdbclient.data.model.error.Error
import coding.legaspi.tmdbclient.utils.MediaPlayerFactory
import coding.legaspi.tmdbclient.utils.MediaPlayerHelper
import com.google.android.material.tabs.TabLayout

class RvEventActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: ActivityRvEventBinding
    private lateinit var dialogHelper: DialogHelper
    private lateinit var mediaPlayerHelper: MediaPlayerHelper

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
        dialogHelper = DialogHelperFactory.create(this)
        mediaPlayerHelper = MediaPlayerFactory.create(this)
        allModelOutput = arrayListOf()
        eventAdapter = EventAdapter(allModelOutput, this)

        binding.loggedInTopNav.labelTitle.text = name
        setBottomButton()
        setMenu()
    }

    private fun setCategory(name: String) {
        binding.rrlFirst.visibility = GONE
        binding.llHistory.visibility = GONE
        binding.llContacts.visibility = GONE
        binding.llHotres.visibility = GONE
        binding.llFoods.visibility = GONE
        binding.llSchool.visibility = GONE
        binding.hymnBar.layoutHymn.visibility = GONE
        Log.i("HYMN", "$name")
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
            "Cavite City Hymn" -> {
                Log.i("HYMN", "Cavite City Hymn")
                binding.noData.visibility = GONE
                binding.rrlFirst.visibility = GONE
                binding.rrMid.visibility = GONE
                binding.rrlSecond.visibility = GONE
                binding.svEvents.visibility = GONE
                binding.hymnBar.layoutHymn.visibility = VISIBLE
                binding.hymnBar.lyricsCategory.visibility = VISIBLE
                binding.hymnBar.hymnTitle.text = getString(R.string.hymn_tagalog_title)
                binding.hymnBar.hymnLyrics.text = getString(R.string.hymn_tagalog_lyrics)
                var lyrics: String? = "Tagalog Hymn"
                binding.hymnBar.lyricsCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        when (tab.position) {
                            0 -> {
                                lyrics = "Tagalog Hymn"
                                binding.hymnBar.hymnTitle.text = getString(R.string.hymn_tagalog_title)
                                binding.hymnBar.hymnLyrics.text = getString(R.string.hymn_tagalog_lyrics)
                            }
                            1 -> {
                                lyrics = "Chabacano Hymn"
                                binding.hymnBar.hymnTitle.text = getString(R.string.hymn_chabacano_title)
                                binding.hymnBar.hymnLyrics.text = getString(R.string.hymn_chabacano_lyrics)
                            }
                            else -> {

                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {
                        mediaPlayerHelper.stopMusic()
                    }
                    override fun onTabReselected(tab: TabLayout.Tab) {
                        mediaPlayerHelper.stopMusic()
                    }
                })
                binding.hymnBar.playSound.setOnClickListener {
                    Log.i("LYRICS PLAY", "$lyrics")
                    mediaPlayerHelper.playMusic(lyrics!!){
                        if (it) mediaPlayerHelper.stopMusic()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayerHelper.stopMusic()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerHelper.stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerHelper.stopMusic()
    }

    val fonts = arrayOf(
        "TAGALOG LYRICS", "CHABACANO LYRICS"
    )

    private fun setBindingSchool() {
        binding.elementary.setOnClickListener {
            setRvCate("Elementary")
            listenToByCatSearch("Elementary")
        }
        binding.highschool.setOnClickListener {
            setRvCate("Highschool")
            listenToByCatSearch("Highschool")
        }
        binding.college.setOnClickListener {
            setRvCate("College")
            listenToByCatSearch("College")
        }
        binding.vocational.setOnClickListener {
            setRvCate("Vocational")
            listenToByCatSearch("Vocational")
        }
        binding.other.setOnClickListener {
            setRvCate("Other")
            listenToByCatSearch("Other")
        }
    }


    private fun setBindingHistory() {
        binding.heroes.setOnClickListener {
            setRvCate("Heroes")
            listenToByCatSearch("Heroes")
        }
        binding.historical.setOnClickListener {
            setRvCate("Historical Places")
            listenToByCatSearch("Historical Places")
        }
        binding.events.setOnClickListener {
            setRvCate("Events")
            listenToByCatSearch("Events")
        }
    }

    private fun setBindingHotel() {
        binding.resort.setOnClickListener {
            setRvCate("Resort")
            listenToByCatSearch("Resort")
        }
        binding.hotel.setOnClickListener {
            setRvCate("Hotel")
            listenToByCatSearch("Hotel")
        }
    }

    private fun setBindingContacts() {
        binding.hospital.setOnClickListener {
            setRvCate("Hospital")
            listenToByCatSearch("Hospital")
        }
        binding.healthCenter.setOnClickListener {
            setRvCate("Health Center")
            listenToByCatSearch("Health Center")
        }
        binding.pharmacy.setOnClickListener {
            setRvCate("Pharmacy")
            listenToByCatSearch("Pharmacy")
        }
        binding.police.setOnClickListener {
            setRvCate("Police Station")
            listenToByCatSearch("Police Station")
        }
        binding.fire.setOnClickListener {
            setRvCate("Fire Station")
            listenToByCatSearch("Fire Station")
        }
    }

    private fun setBindingFoods() {
        binding.cafes.setOnClickListener {
            setRvCate("Cafes")
            listenToByCatSearch("Cafes")
        }
//        binding.stores.setOnClickListener {
//            setRvCate("Convenience Stores")
//            listenToByCatSearch("Convenience Stores")
//        }
        binding.fastfood.setOnClickListener {
            setRvCate("Fast Food")
            listenToByCatSearch("Fast Food")
        }
        binding.homemade.setOnClickListener {
            setRvCate("Homemade Foods")
            listenToByCatSearch("Homemade Foods")
        }
        binding.restaurant.setOnClickListener {
            setRvCate("Restaurants")
            listenToByCatSearch("Restaurants")
        }
        binding.delicacies.setOnClickListener {
            setRvCate("Famous Delicacies")
            listenToByCatSearch("Famous Delicacies")
        }
    }


    private fun listenToByCatSearch(category: String) {
        binding.svEvents.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    binding.progressBar.visibility = VISIBLE
                    try {
                        val responseLiveData = eventViewModel.searchCategory(query, name,category)
                        responseLiveData.observe(this@RvEventActivity, Observer {
                            when(it){
                                is Result.Success<*> -> {
                                    val result = it.data as List<AllModelOutput>
                                    if (result!=null){
                                        allModelOutput.clear()
                                        allModelOutput.addAll(result)
                                        val llm = LinearLayoutManager(this@RvEventActivity, RecyclerView.VERTICAL, false)
                                        binding.epoxyRvAdd.layoutManager = llm
                                        binding.epoxyRvAdd.adapter = eventAdapter
                                        binding.progressBar.visibility = GONE
                                        eventAdapter.setSuggestions(result)
                                        binding.epoxyRvAdd.visibility= VISIBLE

                                    }else{
                                        binding.noData.visibility= VISIBLE
                                        binding.epoxyRvAdd.visibility= GONE
                                        binding.progressBar.visibility = GONE
                                    }
                                }
                                is Result.Error -> {
                                    val exception = it.exception

                                    if (exception is IOException) {
                                        binding.progressBar.visibility = GONE
                                        if (exception.localizedMessage!! == "timeout"){
                                            dialogHelper.showUnauthorizedError(
                                                Error(
                                                    "Server error",
                                                    "Server is down or not reachable ${exception.message}"
                                                ),
                                                positiveButtonFunction = {
                                                    recreate()
                                                }
                                            )
                                        } else{
                                            dialogHelper.showUnauthorizedError(Error("Error",exception.localizedMessage!!),
                                                positiveButtonFunction = {
                                                })
                                        }
                                    } else {
                                        binding.progressBar.visibility = GONE
                                        dialogHelper.showUnauthorizedError(Error("Error","Something went wrong!"),
                                            positiveButtonFunction = {
                                            })
                                    }
                                }
                                Result.Loading -> {
                                    binding.progressBar.visibility = VISIBLE
                                }

                                else -> {}
                            }
                        })
                    } catch (e: IOException) {
                        e.printStackTrace()
                        binding.noData.visibility= VISIBLE
                        binding.epoxyRvAdd.visibility= GONE
                        binding.progressBar.visibility = GONE
                    }
                }else{
                    initRv()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateByCatSuggestions(newText, category)
                return true
            }
        })
    }

    private fun updateByCatSuggestions(newText: String?, category: String) {
        if (newText.isNullOrEmpty()) {
            initRv()
            return
        }
        binding.epoxyRvAdd.visibility = VISIBLE

        try {
            val responseLiveData = eventViewModel.searchCategory(newText, name,category)
            responseLiveData.observe(this, Observer {
                when(it){
                    is Result.Success<*> -> {
                        val result = it.data as List<AllModelOutput>
                        if (result!=null){
                            if (result.isEmpty()){
                                binding.progressBar.visibility = GONE
                                binding.noData.visibility= VISIBLE
                                binding.epoxyRvAdd.visibility= GONE
                                binding.noData.text = "Sorry no data about $newText!"
                            }else{
                                allModelOutput.clear()
                                allModelOutput.addAll(result)
                                val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                                binding.epoxyRvAdd.layoutManager = llm
                                binding.epoxyRvAdd.adapter = eventAdapter
                                binding.progressBar.visibility = GONE
                                binding.noData.visibility = GONE
                                eventAdapter.setSuggestions(result)
                                binding.epoxyRvAdd.visibility= VISIBLE

                            }
                        }else{
                            binding.progressBar.visibility = GONE
                            binding.noData.visibility= VISIBLE
                            binding.epoxyRvAdd.visibility= GONE
                        }
                    }
                    is Result.Error -> {
                        val exception = it.exception

                        if (exception is IOException) {
                            binding.progressBar.visibility = GONE
                            if (exception.localizedMessage!! == "timeout"){
                                dialogHelper.showUnauthorizedError(
                                    Error(
                                        "Server error",
                                        "Server is down or not reachable ${exception.message}"
                                    ),
                                    positiveButtonFunction = {
                                        recreate()
                                    }
                                )
                            } else{
                                dialogHelper.showUnauthorizedError(Error("Error",exception.localizedMessage!!),
                                    positiveButtonFunction = {

                                    })
                            }
                        } else {
                            binding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorizedError(Error("Error","Something went wrong!"),
                                positiveButtonFunction = {

                                })
                        }
                    }
                    Result.Loading -> {
                        binding.progressBar.visibility = VISIBLE
                    }

                    else -> {}
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
            binding.progressBar.visibility = GONE
            binding.noData.visibility= VISIBLE
            binding.epoxyRvAdd.visibility= GONE
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
        setCategory(name)
    }

    private fun initRv() {
        val responseLiveData = eventViewModel.getEventsByCategory(name)
        try {
            responseLiveData.observe(this, Observer {
                when(it){
                    is Result.Success<*> -> {
                        val result = it.data as List<AllModelOutput>
                        if (result!=null){
                            if (result.isEmpty()){
                                binding.noData.visibility=VISIBLE
                                binding.epoxyRvAdd.visibility= GONE
                                binding.labelWelcome.text = "No list for $name"
                            }else{
                                allModelOutput.clear()
                                allModelOutput.addAll(result)
                                val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                                binding.epoxyRvAdd.layoutManager = llm
                                binding.epoxyRvAdd.adapter = eventAdapter
                                binding.labelWelcome.text = "Category $name"
                                binding.epoxyRvAdd.visibility = VISIBLE
                                eventAdapter.notifyDataSetChanged()
                            }
                        }else{
                            binding.noData.visibility=VISIBLE
                            binding.epoxyRvAdd.visibility= GONE
                            Log.d("RvEvent", "Null")
                        }
                    }
                    is Result.Error -> {
                        val exception = it.exception

                        if (exception is IOException) {
                            binding.progressBar.visibility = GONE
                            if (exception.localizedMessage!! == "timeout"){

                            } else{

                            }
                        } else {
                            binding.progressBar.visibility = GONE
                        }
                    }
                    Result.Loading -> {
                        binding.progressBar.visibility = VISIBLE
                    }

                    else -> {}
                }
            })
        }catch (e: Exception){
            binding.noData.visibility=VISIBLE
            binding.epoxyRvAdd.visibility= GONE
        }
    }
}