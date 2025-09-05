package ru.netology.nework.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentLocationPickerBinding
import ru.netology.nework.model.Coordinates
import ru.netology.nework.util.MapUtils

@AndroidEntryPoint
class LocationPickerFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationPickerBinding? = null
    private val binding get() = _binding!!
    
    private var googleMap: GoogleMap? = null
    private var selectedMarker: Marker? = null
    private var selectedCoordinates: Coordinates? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupMap()
        setupButtons()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.title = "Выбор местоположения"
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupButtons() {
        binding.selectLocationButton.setOnClickListener {
            selectedCoordinates?.let { coordinates ->
                val result = Bundle().apply {
                    putDouble("lat", coordinates.lat)
                    putDouble("lng", coordinates.lng)
                }
                parentFragmentManager.setFragmentResult("location_result", result)
                findNavController().navigateUp()
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        MapUtils.setupMap(map)

        val moscow = LatLng(55.7558, 37.6176)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(moscow, 10f))

        map.setOnMapClickListener { latLng ->
            addMarkerAtLocation(latLng)
        }
    }

    private fun addMarkerAtLocation(latLng: LatLng) {
        selectedMarker?.remove()

        selectedMarker = googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Выбранное место")
        )

        selectedCoordinates = Coordinates(
            lat = latLng.latitude,
            lng = latLng.longitude
        )

        binding.selectLocationButton.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
