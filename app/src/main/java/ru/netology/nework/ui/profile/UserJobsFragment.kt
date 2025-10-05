package ru.netology.nework.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentUserJobsBinding

@AndroidEntryPoint
class UserJobsFragment : Fragment() {

    private val viewModel: UserJobsViewModel by viewModels()
    private var _binding: FragmentUserJobsBinding? = null
    private val binding get() = _binding!!
    
    private val userId: Long by lazy {
        (parentFragment as? UserProfileFragment)?.userId ?: 0L
    }

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

        val adapter = UserJobsAdapter()
        binding.jobsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.jobsRecycler.adapter = adapter

        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            adapter.submitList(jobs)
        }

        viewModel.loadUserJobs(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}









