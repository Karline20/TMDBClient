package coding.legaspi.tmdbclient.presentation.about.aboutus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.databinding.ActivityAboutUsBinding
import coding.legaspi.tmdbclient.presentation.about.terms.EditTermsFragment
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.DialogHelperFactory
import coding.legaspi.tmdbclient.utils.FirebaseManager
import javax.inject.Inject

class AboutUsActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding : ActivityAboutUsBinding
    private lateinit var dialogHelper: DialogHelper
    private val editFragment = EditAboutUsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        binding.progressBar.visibility = VISIBLE
        setAboutUs()

        binding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setAboutUs() {
        FirebaseManager().fetchIdAboutFromFirebase {
            val responseLiveData = eventViewModel.getAboutUs(it)
            responseLiveData.observe(this, Observer{
                if (it!=null){
                    val id = it.body()?.id
                    val description = it.body()?.description
                    binding.about2.text = description
                    binding.progressBar.visibility = GONE
                    binding.edit.setOnClickListener {
                        dialogHelper.tutorial("Edit", "Do you want to edit this Terms?", "Yes", "No" ){
                            if (it){
                                val bundle = Bundle()
                                bundle.putString("id",id)
                                editFragment.setEventViewModel(eventViewModel)
                                editFragment.arguments = bundle
                                editFragment.show(supportFragmentManager, "EditAboutUsFragment")
                            }
                        }
                    }
                }
            })
        }

    }
}