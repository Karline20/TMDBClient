package coding.legaspi.tmdbclient.presentation.about.terms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.terms.Terms
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditTermsFragment : BottomSheetDialogFragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var etfTitle: EditText
    private lateinit var etfDesc: EditText
    private lateinit var etsecTitle: EditText
    private lateinit var etSecDesc: EditText
    private lateinit var btn_update: Button
    private lateinit var progressBar: RelativeLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve the Bundle from fragment arguments
        etfTitle = view.findViewById(R.id.etfTitle)
        etfDesc = view.findViewById(R.id.etfDesc)
        etsecTitle = view.findViewById(R.id.etsecTitle)
        etSecDesc = view.findViewById(R.id.etSecDesc)
        btn_update = view.findViewById(R.id.btn_update)
        progressBar = view.findViewById(R.id.progressBar)

        val bundle = arguments
        if (bundle != null) {
            val id = bundle.getString("id")
            setTerms(id)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun setTerms(id: String?) {
        val responseLiveData = eventViewModel.getTerms(id.toString())
        responseLiveData.observe(this, Observer {
            if (it!=null){
                val firstTitle = it.body()?.ftitle
                val firstDesc = it.body()?.fdesc
                val secTitle = it.body()?.stitle
                val secDesc = it.body()?.sdesc
                etfTitle.setText(firstTitle)
                etfDesc.setText(firstDesc)
                etsecTitle.setText(secTitle)
                etSecDesc.setText(secDesc)
            }
        })

        btn_update.setOnClickListener {
            progressBar.visibility = VISIBLE
            val firstTitle = etfTitle.text.toString()
            val firstDesc = etfDesc.text.toString()
            val secTitle = etsecTitle.text.toString()
            val secDesc = etSecDesc.text.toString()

            val responseLiveData = eventViewModel.patchTerms(id.toString(), Terms(firstTitle,firstDesc,secTitle,secDesc))
            responseLiveData.observe(this, Observer {
                if (it!=null){
                    if (it.isSuccessful){
                        progressBar.visibility = GONE
                        dismiss()
                    }
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_terms, container, false)

    }

    fun setEventViewModel(eventViewModel: EventViewModel) {
        this.eventViewModel = eventViewModel
    }

}