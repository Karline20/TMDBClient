package coding.legaspi.tmdbclient.presentation.about.terms

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.tmdbclient.databinding.ActivityTermsBinding
import coding.legaspi.tmdbclient.presentation.about.researcher.AddResearcherActivity
import coding.legaspi.tmdbclient.presentation.addevent.map.MapsFragment
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.DialogHelperFactory
import coding.legaspi.tmdbclient.utils.FirebaseManager
import javax.inject.Inject

class TermsActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var termsBinding: ActivityTermsBinding
    private lateinit var dialogHelper: DialogHelper
    private val editFragment = EditTermsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        termsBinding = ActivityTermsBinding.inflate(layoutInflater)
        val view = termsBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)

        setTerms()

        termsBinding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        setTerms()
        termsBinding.progressBar.visibility = VISIBLE
    }

    override fun onStart() {
        super.onStart()
        setTerms()
        termsBinding.progressBar.visibility = VISIBLE
    }
    private fun setTerms() {
        FirebaseManager().fetchIdTermsFromFirebase {
            Log.d("Terms", it)
            if (it!=null){
                val id = it
                val responseLiveData = eventViewModel.getTerms(id)
                responseLiveData.observe(this, Observer {
                    if (it!=null){
                        val firstTitle = it.body()?.ftitle
                        val firstDesc = it.body()?.fdesc
                        val secTitle = it.body()?.stitle
                        val secDesc = it.body()?.sdesc
                        termsBinding.tt1.text = firstTitle
                        termsBinding.tt2.text = secTitle
                        termsBinding.desc1.text = firstDesc
                        termsBinding.desc2.text = secDesc
                        termsBinding.progressBar.visibility = GONE
                    }
                })

                termsBinding.edit.setOnClickListener {
                    dialogHelper.tutorial("Edit", "Do you want to edit this Terms?", "Yes", "No" ){
                        if (it){
                            val bundle = Bundle()
                            bundle.putString("id",id)
                            editFragment.setEventViewModel(eventViewModel)
                            editFragment.arguments = bundle
                            editFragment.show(supportFragmentManager, "EditTermsFragment")
                        }
                    }
                }
            }
        }

    }
}