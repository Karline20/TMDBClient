package coding.legaspi.tmdbclient.presentation.addevent

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coding.legaspi.tmdbclient.data.model.adaptermodel.Image
import coding.legaspi.tmdbclient.data.model.adaptermodel.ImageItem
import coding.legaspi.tmdbclient.data.model.events.AllModel
import coding.legaspi.tmdbclient.databinding.ActivityAddEventBinding
import coding.legaspi.tmdbclient.presentation.addevent.adapter.ImageAdapterSelection
import coding.legaspi.tmdbclient.presentation.addevent.map.MapsFragment
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.utils.FirebaseManager
import coding.legaspi.tmdbclient.utils.VibrateView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors
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
        imageItem = arrayListOf()
        binding.spinnerHistory.visibility = GONE
        val intent = intent
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
                Log.d("Edit some", "Loading $idPatch")
                patch()
            }
        }
        if (idPatch=="null"){
            //setName()
        }else{
            //patch()
            Log.d("Edit some", "Loading $idPatch")
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

    private var isImageNotEmpty: Boolean = false
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
                    Log.d("Edit some", "Loading $isLoading")
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
                    Log.d("Edit some", "id $id")
                    Log.d("Edit some", "idPatch $idPatch")
                    val responseLiveData = eventViewModel.patchEventsById(idPatch, AllModel(notEmpty, formattedDate, description, eventName, "",
                        lat, eAddress, lon, currentTimeMillis.toString(), eventcategory))
                    responseLiveData.observe(this, Observer {
                        try {
                            // Initialize a counter variable
                            var processedImageCount = 0
                            Log.d("SaveImage", "processedImageCount $processedImageCount")
                            for (imageuri in imageItem) {
                                val image = Uri.parse(imageuri.uri.toString())
                                if (image != null){
                                    Log.d("SaveImage", "SaveImage $image")
                                    if (isImageNotEmpty) {
                                        if (image.scheme == "https" && image.host == "firebasestorage.googleapis.com"){
                                            Log.d("SaveImage", "if image.scheme == https $image")
                                            processedImageCount++
                                            Log.d("SaveImage", "processedImageCount $processedImageCount")
                                        }else{
                                            FirebaseManager().saveUpdateToFirebase(this, idPatch, image){
                                                if (it){
                                                    processedImageCount++
                                                    Log.d("SaveImage", "processedImageCount $processedImageCount")
                                                    Log.d("SaveImage", "processedImageCountsize ${imageItem.size}")
                                                    if (processedImageCount == imageItem.size){
                                                        Log.d("SaveImage", "processedImageCount $processedImageCount")
                                                        isLoading = false
                                                        Log.d("SaveImage", "image.scheme == file $image")
                                                        Log.d("Edit some", "Loading $isLoading")
                                                        setLoading()
                                                        val intent = Intent(this, HomeActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }else{
                                                    isLoading = false
                                                    Log.d("Edit some", "Loading $isLoading")
                                                    setLoading()
                                                    Toast.makeText(this, "Check you internet connection! 1", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Log.d("SaveImage", "image.scheme == https $image")
                                        isLoading = false
                                        Log.d("Edit some", "Loading $isLoading")
                                        setLoading()
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                        if (image.scheme == "https" && image.host == "firebasestorage.googleapis.com"){
                                            Log.d("SaveImage", "if image.scheme == https $image")
                                        }
                                    }
                                }else{
                                    isLoading = false
                                    Log.d("Edit some", "Loading $isLoading")
                                    setLoading()
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }catch (e: Exception){
                            Toast.makeText(this, "Check you internet connection! 2", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            Log.d("Edit some", "Loading $isLoading")
                            setLoading()
                            Toast.makeText(this, "Can't save Image", Toast.LENGTH_SHORT).show()
                        }
                    })
                }catch (e:Exception){
                    isLoading = false
                    Log.d("Edit some", "Loading $isLoading")
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
        databaseReference.child(idPatch).get()
            .addOnSuccessListener {snapshot ->
                if (snapshot.exists()) {
                    var countImage = 0
                    for (snapshot in snapshot.children) {
                        val image = snapshot.getValue(Image::class.java)
                        if (image != null) {
                            imageList.add(image)
                            countImage++
                            Log.d("URI", "countImage: $countImage")
                            Log.d("URI", "children: ${snapshot.children}")
                            if (imageList.isNotEmpty()) {
                                val imageuri = image.imageUri
                                Log.d("URI", "URI: Karlen "+imageuri)
                                val id = image.id
                                val uri = Uri.parse(imageuri.toString())
                                setImageItem(uri, id!!)
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
                                    var processedImageCount = 0
                                    for (imageuri in imageItem) {
                                        val image = Uri.parse(imageuri.uri.toString())
                                        FirebaseManager().saveEventImageToFirebase(
                                            this,
                                            eventID,
                                            image
                                        ) {
                                            if (it) {
                                                processedImageCount++
                                                if (processedImageCount == imageItem.size){
                                                    isLoading = false
                                                    setLoading()
                                                    val intent = Intent(this, HomeActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
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
        val list: List<Pair<String, String>>
        Log.d(TAG, "eventCategory: $eventcategory")
        Log.d(TAG, "category: $categoryPatch")
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

        // Extract the display values (first item of the pair) to show in the spinner
        val displayList = list.map { it.first }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHistory.adapter = adapter

        if (categoryPatch != "null") {
            // Find the index of the item in the list where the second (actual) value matches categoryPatch
            val indexOfMatchingItem = list.indexOfFirst { it.second == categoryPatch }
            if (indexOfMatchingItem != -1) {
                // Set the spinner selection to the corresponding display value
                binding.spinnerHistory.setSelection(indexOfMatchingItem)
            }
        }

        binding.spinnerHistory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedOption = list[position].second
                Log.d("AddEventView", "$selectedOption")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("AddEventView", "0")
            }
        }
    }
    fun getListBasedOnInput(input: String): List<Pair<String, String>> {
        return when (input) {
            "History" -> listOf(
                "Historical Places" to "Historical Places",
                "Events" to "Events",
                "Notable Person" to "Heroes" // Display "Notable Person" but use "Heroes" as the value
            )
            "Foods" -> listOf(
                "Famous Delicacies" to "Famous Delicacies",
                "Restaurants" to "Restaurants",
                "Cafes" to "Cafes",
                "Fast Food" to "Fast Food",
                "Homemade Foods" to "Homemade Foods"
            )
            "Hotel & Resorts" -> listOf(
                "Resort" to "Resort",
                "Hotel" to "Hotel"
            )
            "Emergency Care & Contacts" -> listOf(
                "Hospital" to "Hospital",
                "Pharmacy" to "Pharmacy",
                "Police Station" to "Police Station",
                "Fire Station" to "Fire Station",
                "Health Center" to "Health Center"
            )
            "Schools" -> listOf(
                "Elementary" to "Elementary",
                "Highschool" to "Highschool",
                "College" to "College",
                "Vocational" to "Vocational",
                "Other" to "Other"
            )
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
            isImageNotEmpty = true
            Log.d("Edit some", "$isImageNotEmpty")
            val newSelectedImageUri = Uri.fromFile(file)
            setImageItem(newSelectedImageUri, "")
        }else {
            Toast.makeText(this, "Path is null!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setImageItem(uriForCamera: Uri, id: String) {
        val imageItems = ImageItem(idPatch, id, uriForCamera)
        imageItem.add(imageItems)
        imageAdapterSelection = ImageAdapterSelection(this, imageItem)
        binding.addPhoto.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.addPhoto.adapter = imageAdapterSelection
        //imageAdapterSelection.notifyDataSetChanged()
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