package coding.legaspi.tmdbclient.presentation.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.adaptermodel.Image
import coding.legaspi.tmdbclient.databinding.ActivityViewEventBinding
import coding.legaspi.tmdbclient.presentation.about.AboutActivity
import coding.legaspi.tmdbclient.presentation.addevent.AddEventActivity
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.rvevent.RvEventActivity
import coding.legaspi.tmdbclient.presentation.users.UsersActivity
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.DialogHelperFactory
import coding.legaspi.tmdbclient.utils.ImageAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ViewEventActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var viewEventBinding: ActivityViewEventBinding
    private lateinit var imageList: ArrayList<Image>
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialogHelper: DialogHelper
    lateinit var id : String
    lateinit var name : String
    lateinit var description: String
    lateinit var category: String
    lateinit var location: String
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var date: String
    lateinit var timestamp: String
    lateinit var imageUri: String
    lateinit var eventcategory: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewEventBinding=ActivityViewEventBinding.inflate(layoutInflater)
        val view = viewEventBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .injectViewEventActivity(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        viewEventBinding.progressBar.visibility = GONE

        id = intent.getStringExtra("id").toString()
        name = intent.getStringExtra("name").toString()
        description = intent.getStringExtra("description").toString()
        category = intent.getStringExtra("category").toString()
        location = intent.getStringExtra("location").toString()
        latitude = intent.getStringExtra("latitude").toString()
        longitude = intent.getStringExtra("longitude").toString()
        date = intent.getStringExtra("date").toString()
        timestamp = intent.getStringExtra("timestamp").toString()
        imageUri = intent.getStringExtra("imageUri").toString()
        eventcategory = intent.getStringExtra("eventcategory").toString()

        Log.d("ViewEventActivity 2 ", "ViewEventActivity: "+eventcategory)

        setData()
        setButtonDelete()
        setButtonEdit()
        setBottomButton()
    }

    private fun setBottomButton() {
        viewEventBinding.loggedInBottomNav.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        viewEventBinding.loggedInBottomNav.events.setImageResource(R.drawable.baseline_folder_red)
        viewEventBinding.loggedInBottomNav.home.setImageResource(R.drawable.outline_home_red)

        viewEventBinding.loggedInBottomNav.users.setOnClickListener {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
        viewEventBinding.loggedInBottomNav.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun setButtonEdit() {
        viewEventBinding.edit.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            intent.putExtra("idPatch", id)
            intent.putExtra("namePatch", name)
            intent.putExtra("category", category)
            intent.putExtra("description", description)
            intent.putExtra("date", date)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("location", location)
            intent.putExtra("timestamp", timestamp)
            intent.putExtra("imageUri", imageUri)
            intent.putExtra("eventcategory", eventcategory)
            startActivity(intent)
        }
    }

    private fun setButtonDelete() {
        viewEventBinding.delete.setOnClickListener {
            dialogHelper.delete("Delete", "Are you sure you want to delete?", "Yes", "No") {
                if (it) {
                    viewEventBinding.progressBar.visibility = VISIBLE
                    Log.d("Delete", eventcategory)
                    val responseLiveData = eventViewModel.delEventsById(id)
                    Log.d("Delete", "Delete id: "+id)
                    try {
                        responseLiveData.observe(this, Observer {
                            if(it.isSuccessful){
                                try {
                                    Toast.makeText(this, "Delete successfully...", Toast.LENGTH_SHORT).show()
                                    viewEventBinding.progressBar.visibility = GONE
                                    val intent = Intent(this, RvEventActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }catch (e: Exception){
                                    Toast.makeText(this, "Delete failed isSuccess...", Toast.LENGTH_SHORT).show()
                                    viewEventBinding.progressBar.visibility = GONE
                                }
                            }else{
                                Toast.makeText(this, "Delete failed...", Toast.LENGTH_SHORT).show()
                                viewEventBinding.progressBar.visibility = GONE
                            }
                        })
                    }catch (e: Exception){

                    }
                }
            }
        }
    }

    private fun setData() {
        Log.d("ViewEventActivity 2 ", "Category:: "+category)

        try {
            viewEventBinding.labelName.text = name
            viewEventBinding.labelDescription.text = description
            viewEventBinding.txtAddress.text = location
            viewEventBinding.labelDate.text = date
            viewEventBinding.labelEventCategory.text = eventcategory
            if (category!= null){
                if (category=="Heroes") {
                    viewEventBinding.labelCategory.text = "Notable Person"
                }else{
                    viewEventBinding.labelCategory.text = category
                }
            }else{
                viewEventBinding.labelCategory.visibility = GONE
            }
            initImage()
        }catch (e: Exception){

        }

    }

    private fun initImage() {
        imageList = arrayListOf()

        databaseReference = FirebaseDatabase.getInstance().getReference("Images")
        databaseReference.child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        imageList.clear()
                        for (snapshot in snapshot.children) {
                            val image = snapshot.getValue(Image::class.java)
                            if (image != null) {
                                imageList.add(image)
                            }
                        }
                        imageAdapter = ImageAdapter(imageList, this@ViewEventActivity)
                        viewEventBinding.epoxyImage.adapter = imageAdapter
                        imageAdapter.notifyDataSetChanged()
                    }
                    else {
                        Log.d("error", "error")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("EventAdapter", "rv Image $error")
                }

            })
    }
}