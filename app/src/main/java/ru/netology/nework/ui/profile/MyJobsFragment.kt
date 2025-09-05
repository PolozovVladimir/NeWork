package ru.netology.nework.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserJobsBinding

@AndroidEntryPoint
class MyJobsFragment : Fragment() {

    private val viewModel: MyJobsViewModel by viewModels()
    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MyJobsAdapter { job ->
            // TODO: Show delete confirmation dialog
        }
        binding.jobsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.jobsRecycler.adapter = adapter

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_createJobFragment)
        }

        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            adapter.submitList(jobs)
        }

        viewModel.loadMyJobs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


