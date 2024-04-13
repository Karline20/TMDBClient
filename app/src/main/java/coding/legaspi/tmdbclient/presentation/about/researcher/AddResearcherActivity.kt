package coding.legaspi.tmdbclient.presentation.about.researcher

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.researchers.Researchers
import coding.legaspi.tmdbclient.databinding.ActivityAddResearcherBinding
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.DialogHelperFactory
import coding.legaspi.tmdbclient.utils.FirebaseManager
import com.bumptech.glide.Glide
import javax.inject.Inject

class AddResearcherActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding : ActivityAddResearcherBinding
    private lateinit var dialogHelper: DialogHelper

    val requiredPermissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    var isStorageImagePermitted = false
    var isCameraAccessPermitted = false

    val TAG = "Permission"

    private var uriForCamera: Uri? = null

    var id: String? = null
    var namePatch: String? = null
    var rPositionPatch: String? = null
    var addressPatch: String? = null
    var contactPatch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddResearcherBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)

        if (!isStorageImagePermitted){
            requestPermissionStorageImages()
        }

        id = intent.getStringExtra("id")
        namePatch = intent.getStringExtra("name")
        rPositionPatch = intent.getStringExtra("rposition")
        addressPatch = intent.getStringExtra("address")
        contactPatch = intent.getStringExtra("contact")

        if (id.isNullOrEmpty() && namePatch.isNullOrEmpty() && rPositionPatch.isNullOrEmpty() && addressPatch.isNullOrEmpty() && contactPatch.isNullOrEmpty()){
            setSave()
            listenToCamera()
        }else{
            binding.progressBar.visibility = VISIBLE
            FirebaseManager().fetchResearchFromFirebase(id!!){
                val image = it.imageUri.toString()
                Log.d("UpdateResearch", "${it.imageUri.toString()}")
                Glide.with(this)
                    .load(image)
                    .placeholder(R.drawable.person_red)
                    .error(R.drawable.person_red)
                    .into(binding.img)
                setSave()
                listenToCamera()
                setData()
            }
        }

        binding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun setData() {
        binding.etname.setText(namePatch)
        binding.etposition.setText(rPositionPatch)
        binding.etaddress.setText(addressPatch)
        binding.etcontact.setText(contactPatch)
        binding.progressBar.visibility = GONE
    }

    override fun onStart() {
        super.onStart()
//        if (!isStorageImagePermitted){
//            requestPermissionStorageImages()
//        }
    }

    override fun onResume() {
        super.onResume()
//        if (!isStorageImagePermitted){
//            requestPermissionStorageImages()
//        }
    }

    private fun listenToCamera() {
        binding.imgClick.setOnClickListener {
            if (isCameraAccessPermitted){
                openCameraOrGallery()
            }else{
                requestPermissionCameraAccess()
            }
        }
    }

    private fun setSave() {
        binding.btnSaveEvent.setOnClickListener {
            etvalidate()
        }
    }

    private fun etvalidate() {
        val name = binding.etname.text.toString()
        val position  = binding.etposition.text.toString()
        val address = binding.etaddress.text.toString()
        val contact  = binding.etcontact.text.toString()
        if (name.isEmpty()){
            binding.etname.error = "Empty field"
        }else if (position.isEmpty()){
            binding.etposition.error = "Empty field"
        }else if (address.isEmpty()){
            binding.etaddress.error = "Empty field"
        }else if (contact.isEmpty()){
            binding.etcontact.error = "Empty field"
        }else{
            binding.progressBar.visibility = VISIBLE
            if (id.isNullOrEmpty() && namePatch.isNullOrEmpty() && rPositionPatch.isNullOrEmpty() && addressPatch.isNullOrEmpty() && contactPatch.isNullOrEmpty()){
                saveToDb(name, position, address, contact)
            }else{
                Log.d("Researcher", "1:"+id.toString())
                patchToDb(id, name, position, address, contact)
            }

        }
    }

    private fun patchToDb(id: String?, name: String, position: String, address: String, contact: String) {
        Log.d("Researcher", "2:$id")
        if (uriForCamera?.equals("") == false){
        val responseLiveData = eventViewModel.patchResearchers(id.toString(), Researchers(name, position, address, contact))
        responseLiveData.observe(this, Observer {
            if (it!=null){
                FirebaseManager().saveRImageToFirebase(this, id.toString(), uriForCamera!!){
                    if (it){
                        binding.progressBar.visibility = GONE
                        dialogHelper.thanksSuccess("New $position", "You have successfully added new $position"){
                            if (it){
                                val intent = Intent(this, ResearcherActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }else{
                        binding.progressBar.visibility = GONE
                    }
                }
            }else{
                binding.progressBar.visibility = GONE
            }
        })
        }else{
            binding.progressBar.visibility = GONE
            Toast.makeText(this, "Error no image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToDb(name: String, position: String, address: String, contact: String) {
        if (uriForCamera?.equals("") == false){
            val responseLiveData = eventViewModel.postResearch(Researchers(name, position, address, contact))
            responseLiveData.observe(this, Observer {
                if (it!=null){
                    val id = it.body()?.id
                    FirebaseManager().saveRImageToFirebase(this, id.toString(), uriForCamera!!){
                        if (it){
                            binding.progressBar.visibility = GONE
                            dialogHelper.thanksSuccess("New $position", "You have successfully added new $position"){
                                if (it){
                                    val intent = Intent(this, ResearcherActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }else{
                            binding.progressBar.visibility = GONE
                        }
                    }
                }else{
                    binding.progressBar.visibility = GONE
                }
            })
        }else{
            binding.progressBar.visibility = GONE
            Toast.makeText(this, "Error no image", Toast.LENGTH_SHORT).show()
        }

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

    private val pickImageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    if (data.data != null) {
                        val imageUri: Uri = data.data!!
                        uriForCamera = Uri.parse(imageUri.toString())
                        binding.img.setImageURI(uriForCamera)
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
        pickImageActivityResultLauncher.launch(intent)
    }

    private val launcherForCamera: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uriForCamera = uriForCamera
                binding.img.setImageURI(uriForCamera)
            }
        }
}