package coding.legaspi.tmdbclient.presentation.addevent

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coding.legaspi.tmdbclient.data.model.adaptermodel.Image
import coding.legaspi.tmdbclient.data.model.adaptermodel.ImageItem
import coding.legaspi.tmdbclient.data.model.events.AllModel
import coding.legaspi.tmdbclient.databinding.ActivityAddEventBinding
import coding.legaspi.tmdbclient.presentation.addevent.adapter.ImageAdapterSelection
import coding.legaspi.tmdbclient.presentation.addevent.adapter.SearchListAdapter
import coding.legaspi.tmdbclient.presentation.addevent.map.MapsFragment
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.utils.FirebaseManager
import coding.legaspi.tmdbclient.utils.ImageAdapter
import coding.legaspi.tmdbclient.utils.VibrateView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class AddEventActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: ActivityAddEventBinding
    private lateinit var imageAdapterSelection: ImageAdapterSelection
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageItem: ArrayList<ImageItem>
    private lateinit var imageList: ArrayList<Image>
    val intentName = listOf("History", "Foods", "Hotel & Resorts", "Emergency Care & Contacts")
    lateinit var id : String
    lateinit var name : String
    private val mapsFragment = MapsFragment()

    lateinit var eventName: String
    lateinit var description: String
    var selectedOption: String? = ""
    var eventAddress: String? = ""
    var latitude: String? = ""
    var longitude: String? = ""

    lateinit var idPatch : String
    lateinit var namePatch : String
    lateinit var descriptionPatch : String
    lateinit var categoryPatch : String
    lateinit var locationPatch : String
    lateinit var latitudePatch : String
    lateinit var longitudePatch : String
    lateinit var datePatch : String
    lateinit var timestampPatch : String
    lateinit var imageUriPatch : String
    lateinit var eventcategory : String

    val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    var isStorageImagePermitted = false
    var isCameraAccessPermitted = false

    val TAG = "Permission"

    private var uriForCamera: Uri? = null
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .injectAddEventActivity(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        binding.spinnerHistory.visibility = GONE
        val intent = intent
        imageItem = arrayListOf()
        imageAdapterSelection = ImageAdapterSelection(this, imageItem)
        id = intent.getStringExtra("id").toString()
        name = intent.getStringExtra("name").toString()
        idPatch = intent.getStringExtra("idPatch").toString()
        namePatch = intent.getStringExtra("namePatch").toString()
        descriptionPatch = intent.getStringExtra("description").toString()
        categoryPatch = intent.getStringExtra("category").toString()
        locationPatch = intent.getStringExtra("location").toString()
        latitudePatch = intent.getStringExtra("latitude").toString()
        longitudePatch = intent.getStringExtra("longitude").toString()
        datePatch = intent.getStringExtra("date").toString()
        timestampPatch = intent.getStringExtra("timestamp").toString()
        imageUriPatch = intent.getStringExtra("imageUri").toString()
        eventcategory = intent.getStringExtra("eventcategory").toString()

        Log.d("Image", "idPatch" + idPatch)
        Log.d("Image", "eventcategory" + eventcategory)
        Log.d("Image", "categoryPatch" + categoryPatch)
        binding.loggedInTopNav.labelWelcome.text=name
        isLoading = false
        listentToSpinner()
        listenToCamera()

        binding.btnSaveEvent.setOnClickListener {
            if (idPatch=="null"){
                setName()
            }else{
                patch()
            }
        }
        if (idPatch=="null"){
            //setName()
        }else{
            //patch()
            setData()
        }
        binding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.etaddress.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentByTag("MapsFragment")
            if (fragment == null) {
                val bundle = Bundle()
                mapsFragment.arguments = bundle
                mapsFragment.show(supportFragmentManager, "MapsFragment")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun checkLocation() {
        mapsFragment.setLocationSelectedListener(object : MapsFragment.OnLocationSelectedListener {
            override fun onLocationSelected(address: String?, clatitude: Double?, clongitude: Double?) {
                if (address != null && clatitude != null && clongitude != null) {
                    eventAddress = address
                    latitude = clatitude.toString()
                    longitude = clongitude.toString()
                    binding.etaddress.setText(eventAddress)
                }
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun patch() {
        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(currentTimeMillis)
        val formattedDate = sdf.format(date)
            if (validate()){
                try {
                    var notEmpty: String = selectedOption!!.ifEmpty { "" }
                    isLoading = true
                    setLoading()
                    val lat: String
                    val lon: String
                    val eAddress: String
                    if (latitude!!.isEmpty() && longitude!!.isEmpty() ){
                        lat = latitudePatch
                        lon = longitudePatch
                        eAddress = locationPatch
                    }else{
                        lat = latitude!!
                        lon = longitude!!
                        eAddress = eventAddress!!
                    }

                    val responseLiveData = eventViewModel.patchEventsById(id, AllModel(notEmpty, formattedDate, description, eventName, "",
                        lat, eAddress, lon, currentTimeMillis.toString(), eventName))
                    responseLiveData.observe(this, Observer {
                        try {
                            for (imageuri in imageItem) {
                                val image = Uri.parse(imageuri.uri.toString())
                                FirebaseManager().saveUpdateToFirebase(this, idPatch, image){
                                    if (it){
                                        isLoading = false
                                        setLoading()
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        isLoading = false
                                        setLoading()
                                        Toast.makeText(this, "Check you internet connection! 1", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }catch (e: Exception){
                            Toast.makeText(this, "Check you internet connection! 2", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            setLoading()
                            Toast.makeText(this, "Can't save Image", Toast.LENGTH_SHORT).show()
                        }
                    })
                }catch (e:Exception){
                    isLoading = false
                    setLoading()
                    Log.e("ADD EVENT", "$e")
                    Toast.makeText(this, "Check you internet connection! 3", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setData() {
        binding.etname.setText(namePatch)
        binding.etdesc.setText(descriptionPatch)
        binding.etaddress.setText(locationPatch)
        binding.loggedInTopNav.labelWelcome.text=eventcategory
        setListImage()
    }

    private fun setListImage() {
        imageList = arrayListOf()
        databaseReference = FirebaseDatabase.getInstance().getReference("Images")
        databaseReference.child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snapshot in snapshot.children) {
                            val image = snapshot.getValue(Image::class.java)
                            if (image != null) {
                                imageList.add(image)
                                if (imageList.isNotEmpty()) {
                                    val imageuri = imageList[0].imageUri
                                    val uri = Uri.parse(imageuri.toString())
                                    setImageItem(uri)
                                    Log.d("URI", "URI: Karlen "+imageuri)
                                }
                                //
                            }else{
                                Log.d("URI", "URI: Karlen ")
                            }
                        }
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

    private fun setLoading() {
        when (isLoading){
            true -> {
                binding.progressBar.visibility = VISIBLE
            }
            false ->{
                binding.progressBar.visibility = GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isStorageImagePermitted){
            requestPermissionStorageImages()
        }
    }
    private fun setName() {
        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(currentTimeMillis)

        val formattedDate = sdf.format(date)
            if (validate()){
                try {
                    isLoading = true
                    setLoading()
                    var notEmpty = selectedOption!!.ifBlank{ "" }
                    val responseLiveData = eventViewModel.postEvents(AllModel(notEmpty, formattedDate, description, eventName, "",
                        latitude!!, eventAddress!!, longitude!!, currentTimeMillis.toString(), name))
                    responseLiveData.observe(this, Observer {
                        try {
                            if (it!=null){
                                val eventID = it.body()?.id
                                Log.d("AddEvent", "$eventID")
                                if (eventID!=null){
                                    for (imageuri in imageItem) {
                                        val image = Uri.parse(imageuri.uri.toString())
                                        FirebaseManager().saveEventImageToFirebase(
                                            this,
                                            eventID,
                                            image
                                        ) {
                                            if (it) {
                                                isLoading = false
                                                setLoading()
                                                val intent = Intent(this, HomeActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                isLoading = false
                                                setLoading()
                                                Toast.makeText(
                                                    this,
                                                    "Check you internet connection! 4",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }else{
                                    isLoading = false
                                    setLoading()
                                    Toast.makeText(this, "Can't save Image", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                isLoading = false
                                setLoading()
                                Toast.makeText(this, "Can't save the data!", Toast.LENGTH_SHORT).show()
                            }                        }catch (e: Exception){
                            Toast.makeText(this, "Check you internet connection! 5", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            setLoading()
                            Toast.makeText(this, "Can't save Image", Toast.LENGTH_SHORT).show()
                        }
                    })
                }catch (e:Exception){
                    isLoading = false
                    setLoading()
                    Log.e("ADD EVENT", "$e")
                    Toast.makeText(this, "Check you internet connection! 6", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun listentToSpinner() {
        val list: List<String>
        Log.d(TAG, "eventCategory: $eventcategory")
        if (categoryPatch=="null") {
            list = getListBasedOnInput(name)
            if (list.isEmpty()){
                binding.spinnerHistory.visibility = GONE
            }else{
                binding.spinnerHistory.visibility = VISIBLE
            }
        }else{
            Log.d(TAG, "notEmpty")
            list = getListBasedOnInput(eventcategory)
            if (list.isEmpty()){
                binding.spinnerHistory.visibility = GONE
            }else{
                binding.spinnerHistory.visibility = VISIBLE
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHistory.adapter = adapter

        binding.spinnerHistory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedOption = list[position]
                Log.d("AddEventView", "$selectedOption")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("AddEventView", "0")
            }
        }
    }

    fun getListBasedOnInput(input: String): List<String> {
        return when (input) {
            "History" -> listOf("Historical Places", "Events", "Heroes")
            "Foods" -> listOf("Famous Delicacies", "Restaurants", "Cafes", "Convenience Stores", "Fast Food", "Homemade Foods")
            "Hotel & Resorts" -> listOf("Resort", "Hotel")
            "Emergency Care & Contacts" -> listOf("Hospital", "Pharmacy", "Police Station", "Fire Station")
            "Schools" -> listOf("Elementary", "Highschool", "College", "Vocational", "Other")
//            "Church" -> listOf("Church")
            else -> emptyList()
        }
    }

    private fun listenToCamera() {
        binding.txtAddPhoto.setOnClickListener {
            if (isCameraAccessPermitted){
                openCameraOrGallery()
            }else{
                requestPermissionCameraAccess()
            }
        }
    }

    private fun validateEt(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var isValid = true
        var errorMessage: String? = null
        eventName = binding.etname.text.toString()
        description = binding.etdesc.text.toString()

        if (eventName.isEmpty()) {
            errorMessage = "Name is required"
            isValid = false
            binding.etname.setText(errorMessage)
        }

        if (description.isEmpty()) {
            errorMessage = "Description is required"
            isValid = false
            binding.etname.setText(errorMessage)
        }

        return isValid
    }

    private fun validate(): Boolean {
        var isValid = true
        if (!validateEt(shouldVibrate = false)) {
            isValid = false
        }

        if (!isValid) {
            VibrateView.vibrate(this, binding.ll)
        }

        return isValid
    }

    fun requestPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(this, requiredPermissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "${requiredPermissions[0]} Granted")
            isStorageImagePermitted = true
            requestPermissionCameraAccess()
        } else {
            requestPermissionLauncherStorageImages.launch(requiredPermissions[0])
        }
    }

    private val requestPermissionLauncherStorageImages: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "${requiredPermissions[0]} Granted")
                isStorageImagePermitted = true
                requestPermissionCameraAccess()
            } else {
                Log.d(TAG, "${requiredPermissions[0]} Not Granted")
                isStorageImagePermitted = false
            }
        }

    private fun requestPermissionCameraAccess() {
        if (ContextCompat.checkSelfPermission(this, requiredPermissions[1]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "${requiredPermissions[1]} Granted")
            isCameraAccessPermitted = true
        } else {
            requestPermissionLauncherCameraAccess.launch(requiredPermissions[1])
        }
    }

    private val requestPermissionLauncherCameraAccess: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "${requiredPermissions[1]} Granted")
            } else {
                Log.d(TAG, "${requiredPermissions[1]} Not Granted")
                isCameraAccessPermitted = false
            }
        }


    fun getPathFromURI(contentUri: Uri?): String? {
        var res: String? = null

        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentUri?.let { contentResolver.query(it, proj, null, null, null) }
        cursor?.use {
            if (it.moveToFirst()) {
                val column_index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                res = it.getString(column_index)
            }
        }

        return res
    }

    private fun handleSelectedImage(imageUri: Uri) {
        val path = getPathFromURI(imageUri)
        if (path != null) {
            val file = File(path)
            val newSelectedImageUri = Uri.fromFile(file)
            setImageItem(newSelectedImageUri)
        }
    }

    private fun setImageItem(uriForCamera: Uri) {
        val imageItems = ImageItem(uriForCamera)
        imageItem.add(imageItems)
        binding.addPhoto.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.addPhoto.adapter = imageAdapterSelection
        imageAdapterSelection.notifyDataSetChanged()
    }

    private val pickImageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    if (data.clipData != null) {
                        for (i in 0 until data.clipData!!.itemCount) {
                            val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                            handleSelectedImage(imageUri)
                        }
                    } else if (data.data != null) {
                        val imageUri: Uri = data.data!!
                        handleSelectedImage(imageUri)
                    }
                }
            }
        }

    fun openCameraOrGallery() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
            .setItems(arrayOf("Open Camera", "Open Gallery")) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Legaspi")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Captured by Legaspi")
        uriForCamera = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForCamera)
        launcherForCamera.launch(cameraIntent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickImageActivityResultLauncher.launch(intent)
    }

    private val launcherForCamera: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    handleSelectedImage(selectedImageUri)
                } else {
                    if (uriForCamera != null) {
                        handleSelectedImage(uriForCamera!!)
                    }
                }
            }
        }
}