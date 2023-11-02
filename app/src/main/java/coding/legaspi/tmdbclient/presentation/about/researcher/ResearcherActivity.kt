package coding.legaspi.tmdbclient.presentation.about.researcher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.researchers.ResearchersOutput
import coding.legaspi.tmdbclient.databinding.ActivityResearcherBinding
import coding.legaspi.tmdbclient.presentation.about.researcher.adapter.ResearcherAdapter
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.DialogHelperFactory
import javax.inject.Inject

class ResearcherActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding : ActivityResearcherBinding
    private lateinit var researchersOutput: ArrayList<ResearchersOutput>
    private lateinit var aboutAdapter: ResearcherAdapter
    private lateinit var dialogHelper: DialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResearcherBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        researchersOutput = arrayListOf()
        aboutAdapter = ResearcherAdapter(researchersOutput,this , this, eventViewModel, dialogHelper)

        binding.addResearcher.setOnClickListener {
            val intent = Intent(this, AddResearcherActivity::class.java)
            startActivity(intent)
        }
        binding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.progressBar.visibility = View.VISIBLE
        setRvResearcher()


    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.VISIBLE
        setRvResearcher()


    }

    private fun setRvResearcher() {
        val responseLiveData = eventViewModel.getReserachers()
        responseLiveData.observe(this, Observer{
            if (it!=null){
                if (it.isEmpty()){
                    binding.noData.visibility = View.VISIBLE
                    binding.rvResearcher.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }else{
                    researchersOutput.clear()
                    researchersOutput.addAll(it)
                    val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    binding.rvResearcher.layoutManager = llm
                    binding.rvResearcher.adapter = aboutAdapter
                    binding.noData.visibility= View.GONE
                    aboutAdapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

}