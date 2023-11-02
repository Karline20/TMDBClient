package coding.legaspi.tmdbclient.presentation.about.aboutus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.aboutus.AboutUs
import coding.legaspi.tmdbclient.data.model.terms.Terms
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditAboutUsFragment : BottomSheetDialogFragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var etDesc: EditText
    private lateinit var btn_update: Button
    private lateinit var progressBar: RelativeLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etDesc = view.findViewById(R.id.etDesc)
        btn_update = view.findViewById(R.id.btn_update)
        progressBar = view.findViewById(R.id.progressBar)

        val bundle = arguments
        if (bundle != null) {
            val id = bundle.getString("id")
            setAbout(id)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setAbout(id: String?) {
        val responseLiveData = eventViewModel.getAboutUs(id.toString())
        responseLiveData.observe(this, Observer{
            if (it!=null){
                val id = it.body()?.id
                val description = it.body()?.description
                etDesc.setText(description)

                btn_update.setOnClickListener {
                    progressBar.visibility = View.VISIBLE
                    val description = etDesc.text.toString()

                    val responseLiveData = eventViewModel.patchAboutUs(id.toString(), AboutUs(description))
                    responseLiveData.observe(this, Observer {
                        if (it!=null){
                            if (it.isSuccessful){
                                progressBar.visibility = View.GONE
                                dismiss()
                            }
                        }
                    })
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_about_us, container, false)
    }

    fun setEventViewModel(eventViewModel: EventViewModel) {
        this.eventViewModel = eventViewModel
    }

}