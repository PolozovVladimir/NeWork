package ru.netology.nework.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentLocationPickerBinding
import ru.netology.nework.model.Coordinates

@AndroidEntryPoint
class LocationPickerFragment : Fragment() {

    private var _binding: FragmentLocationPickerBinding? = null
    private val binding get() = _binding!!
    
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


    private fun addMarkerAtLocation(lat: Double, lng: Double) {
        selectedCoordinates = Coordinates(
            lat = lat,
            lng = lng
        )

        binding.selectLocationButton.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
