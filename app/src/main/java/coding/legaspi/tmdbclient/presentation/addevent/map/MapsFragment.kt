package coding.legaspi.tmdbclient.presentation.addevent.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.error.Error
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.DialogHelperFactory

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.IOException

class MapsFragment : DialogFragment() {

    private var locationSelectedListener: OnLocationSelectedListener? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var search: SearchView
    private lateinit var btn_proceed: Button
    private var marker: Marker? = null
    private var checkAddress: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var dialogHelper: DialogHelper
    private var isSearch: Boolean = false

    fun setLocationSelectedListener(listener: OnLocationSelectedListener) {
        locationSelectedListener = listener
    }

    interface OnLocationSelectedListener {
        fun onLocationSelected(address: String?, latitude: Double?, longitude: Double?)
    }

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        val latLng = LatLng(14.479129700000001, 120.8969634)
        val customLocationBitmap = vectorToBitmap(R.drawable.baseline_place_24)
        marker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(customLocationBitmap))
                .title("Cavite City"))
        marker?.showInfoWindow()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        dialogHelper = DialogHelperFactory.create(requireContext())
        search = view.findViewById(R.id.search)
        btn_proceed = view.findViewById(R.id.btn_proceed)
        Log.d("LOCATION", "onViewCreated $isSearch")
        btn_proceed.setOnClickListener {
            if (isSearch){
                locationSelectedListener?.onLocationSelected(checkAddress, latitude, longitude)
                dismiss()
            }else{
                dialogHelper.showError(Error("Please search some address first!", "No address"))
            }
        }
        searching()
    }

    private fun searching() {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val location = search.query.toString()
                    var addressList: List<Address>? = null
                    if (location != null){
                        val geocoder = Geocoder(context!!)
                        try {
                            addressList = geocoder.getFromLocationName(location, 1)!!
                        }catch (e:IOException){
                            e.printStackTrace()
                        }
                        val address: Address = addressList!![0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        Log.d("LOCATION", "$latLng")
                        val customLocationBitmap = vectorToBitmap(R.drawable.baseline_place_24)
                        marker?.remove()
                        val adminArea = address.adminArea
                        val subAdminArea = address.subAdminArea
                        val countryName = address.countryName
                        val postalCode = address.postalCode
                        val countryCode = address.countryCode
                        val locale = address.locale
                        val subLocality = address.subLocality
                        val locality = address.locality
                        val maxAddressLineIndex = address.maxAddressLineIndex
                        val premises = address.premises
                        if (adminArea?.isEmpty() == true &&
                            subAdminArea?.isEmpty() == true &&
                            subLocality?.isEmpty() == true &&
                            locality?.isEmpty() == true &&
                            postalCode?.isEmpty() == true &&
                            countryName?.isEmpty() == true) {
//                        checkAddress = "$subLocality, $locality, $postalCode, $subAdminArea, $adminArea, $countryName"
//                        longitude = address.longitude
//                        latitude = address.latitude
                            isSearch = false
                            Log.d("LOCATION", "searching $isSearch")

                            Log.d("LOCATION", "adminArea $adminArea")
                            Log.d("LOCATION", "subAdminArea $subAdminArea")
                            Log.d("LOCATION", "countryName$countryName")
                            Log.d("LOCATION", "postalCode $postalCode")
                            Log.d("LOCATION", "countryCode$countryCode")
                            Log.d("LOCATION", "locale$locale")
                            Log.d("LOCATION", "subLocality $subLocality")
                            Log.d("LOCATION", "locality $locality")
                            Log.d("LOCATION", "maxAddressLineIndex $maxAddressLineIndex")
                            Log.d("LOCATION", "premises $premises")
                        } else {
//                        checkAddress = "$subLocality, $locality, $postalCode, $subAdminArea, $adminArea, $countryName"
                            checkAddress = "$subLocality $locality $postalCode $subAdminArea $adminArea, $countryName".replace("null", "")
                            longitude = address.longitude
                            latitude = address.latitude
                            isSearch = true
                            Log.d("LOCATION", "searching $isSearch")

                            Log.d("LOCATION", "adminArea $adminArea")
                            Log.d("LOCATION", "subAdminArea $subAdminArea")
                            Log.d("LOCATION", "countryName$countryName")
                            Log.d("LOCATION", "postalCode $postalCode")
                            Log.d("LOCATION", "countryCode$countryCode")
                            Log.d("LOCATION", "locale$locale")
                            Log.d("LOCATION", "subLocality $subLocality")
                            Log.d("LOCATION", "locality $locality")
                            Log.d("LOCATION", "maxAddressLineIndex $maxAddressLineIndex")
                            Log.d("LOCATION", "premises $premises")
                        }

                        marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(customLocationBitmap))
                                .title(checkAddress))
                        marker?.showInfoWindow()
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        Log.d("LOCATION", "$latLng")
                    }
                    return true
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun vectorToBitmap(vectorDrawable: Int): Bitmap {
        val vector = VectorDrawableCompat.create(resources, vectorDrawable, null)
            ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val bitmap = Bitmap.createBitmap(
            vector.intrinsicWidth,
            vector.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vector.setBounds(0, 0, canvas.width, canvas.height)
        vector.draw(canvas)
        return bitmap
    }
}