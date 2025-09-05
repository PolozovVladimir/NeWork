package ru.netology.nework.ui.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreateEventBinding
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateEventFragment : Fragment() {

    private val viewModel: CreateEventViewModel by viewModels()
    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!
    private var selectedDateTime: Date? = null
    private var selectedCoordinates: ru.netology.nework.model.Coordinates? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventTypeRadioButtons()
        setupDateTimePicker()
        setupMediaButtons()
        setupLocationButtons()
        observeViewModel()
    }

    private fun setupEventTypeRadioButtons() {
        binding.onlineRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.offlineRadio.isChecked = false
            }
        }
        binding.offlineRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.onlineRadio.isChecked = false
            }
        }
    }

    private fun setupDateTimePicker() {
        binding.eventDatetime.setOnClickListener {
            showDateTimePicker()
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        selectedDateTime?.let { calendar.time = it }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val dateCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                showTimePicker(dateCalendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(dateCalendar: Calendar) {
        val calendar = Calendar.getInstance()
        selectedDateTime?.let { calendar.time = it }

        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                dateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                dateCalendar.set(Calendar.MINUTE, minute)
                selectedDateTime = dateCalendar.time
                updateDateTimeDisplay()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateDateTimeDisplay() {
        selectedDateTime?.let { date ->
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            binding.eventDatetime.setText(formatter.format(date))
        }
    }

    private fun setupMediaButtons() {
        binding.selectImageButton.setOnClickListener {
            // TODO: Implement image selection
            Toast.makeText(requireContext(), "Будет реализовано позже", Toast.LENGTH_SHORT).show()
        }

        binding.selectAudioButton.setOnClickListener {
            // TODO: Implement audio selection
            Toast.makeText(requireContext(), "Будет реализовано позже", Toast.LENGTH_SHORT).show()
        }

        binding.selectVideoButton.setOnClickListener {
            // TODO: Implement video selection
            Toast.makeText(requireContext(), "Будет реализовано позже", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLocationButtons() {
        binding.selectLocationButton.setOnClickListener {
            findNavController().navigate(R.id.locationPickerFragment)
        }

        binding.selectSpeakersButton.setOnClickListener {
            // TODO: Implement speakers selection
            Toast.makeText(requireContext(), "Будет реализовано позже", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.eventCreated.observe(viewLifecycleOwner) { created ->
            if (created) {
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        // Обработка результата выбора местоположения
        parentFragmentManager.setFragmentResultListener("location_result", viewLifecycleOwner) { _, result ->
            val lat = result.getDouble("lat")
            val lng = result.getDouble("lng")
            selectedCoordinates = ru.netology.nework.model.Coordinates(lat, lng)
            binding.selectLocationButton.text = "Место выбрано"
            Toast.makeText(requireContext(), "Местоположение выбрано", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_event_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_event -> {
                saveEvent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveEvent() {
        val content = binding.content.text.toString().trim()

        if (content.isBlank()) {
            binding.contentInput.error = "Описание события не может быть пустым"
            return
        }

        if (selectedDateTime == null) {
            binding.eventDatetimeInput.error = "Выберите дату и время события"
            return
        }

        val link = binding.link.text.toString().trim()
        val linkToSave = if (link.isBlank()) null else link

        val eventType = if (binding.onlineRadio.isChecked) "ONLINE" else "OFFLINE"
        val datetime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(selectedDateTime!!)

        viewModel.createEvent(
            content = content,
            link = linkToSave,
            datetime = datetime,
            type = eventType,
            coords = selectedCoordinates
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


