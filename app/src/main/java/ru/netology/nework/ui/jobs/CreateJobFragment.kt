package ru.netology.nework.ui.jobs

import android.app.DatePickerDialog
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
import ru.netology.nework.databinding.FragmentCreateJobBinding
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateJobFragment : Fragment() {

    private val viewModel: CreateJobViewModel by viewModels()
    private var _binding: FragmentCreateJobBinding? = null
    private val binding get() = _binding!!
    private var selectedStartDate: Date? = null
    private var selectedFinishDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatePickers()
        setupCurrentJobCheckbox()
        observeViewModel()
    }

    private fun setupDatePickers() {
        binding.startDate.setOnClickListener {
            showDatePicker { date ->
                selectedStartDate = date
                updateStartDateDisplay()
            }
        }

        binding.finishDate.setOnClickListener {
            showDatePicker { date ->
                selectedFinishDate = date
                updateFinishDateDisplay()
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateStartDateDisplay() {
        selectedStartDate?.let { date ->
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            binding.startDate.setText(formatter.format(date))
        }
    }

    private fun updateFinishDateDisplay() {
        selectedFinishDate?.let { date ->
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            binding.finishDate.setText(formatter.format(date))
        }
    }

    private fun setupCurrentJobCheckbox() {
        binding.currentJobCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedFinishDate = null
                binding.finishDate.setText("")
                binding.finishDateInput.isEnabled = false
            } else {
                binding.finishDateInput.isEnabled = true
            }
        }
    }

    private fun observeViewModel() {
        viewModel.jobCreated.observe(viewLifecycleOwner) { created ->
            if (created) {
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_job_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_job -> {
                saveJob()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveJob() {
        val companyName = binding.companyName.text.toString().trim()
        val position = binding.position.text.toString().trim()

        if (companyName.isBlank()) {
            binding.companyNameInput.error = "Название компании не может быть пустым"
            return
        }

        if (position.isBlank()) {
            binding.positionInput.error = "Должность не может быть пустой"
            return
        }

        if (selectedStartDate == null) {
            binding.startDateInput.error = "Выберите дату начала работы"
            return
        }

        val link = binding.link.text.toString().trim()
        val linkToSave = if (link.isBlank()) null else link

        val startDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(selectedStartDate!!)
        val finishDate = if (binding.currentJobCheckbox.isChecked) {
            null
        } else {
            selectedFinishDate?.let { 
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(it)
            }
        }

        viewModel.createJob(
            companyName = companyName,
            position = position,
            startDate = startDate,
            finishDate = finishDate,
            link = linkToSave
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}











