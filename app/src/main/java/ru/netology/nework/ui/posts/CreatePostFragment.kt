package ru.netology.nework.ui.posts

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreatePostBinding
import ru.netology.nework.model.Attachment
import ru.netology.nework.model.AttachmentType
import ru.netology.nework.util.MediaUtils
import java.io.File

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private val viewModel: CreatePostViewModel by viewModels()
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private var selectedAttachment: Attachment? = null
    private var selectedCoordinates: ru.netology.nework.model.Coordinates? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleImageSelection(uri)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        android.util.Log.d("CreatePostFragment", "Fragment created")

                           binding.selectImageButton.setOnClickListener {
                       val intent = MediaUtils.createImagePickerIntent()
                       imagePickerLauncher.launch(intent)
                   }

        binding.selectAudioButton.setOnClickListener {
            Toast.makeText(requireContext(), "Будет реализовано позже", Toast.LENGTH_SHORT).show()
        }

        binding.selectVideoButton.setOnClickListener {
            Toast.makeText(requireContext(), "Будет реализовано позже", Toast.LENGTH_SHORT).show()
        }

        binding.selectLocationButton.setOnClickListener {
            findNavController().navigate(R.id.locationPickerFragment)
        }

        binding.selectMentionsButton.setOnClickListener {
            findNavController().navigate(R.id.selectUsersFragment)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) { created ->
            if (created) {
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }


        parentFragmentManager.setFragmentResultListener("location_result", viewLifecycleOwner) { _, result ->
            val lat = result.getDouble("lat")
            val lng = result.getDouble("lng")
            selectedCoordinates = ru.netology.nework.model.Coordinates(lat, lng)
            binding.selectLocationButton.text = "Место выбрано"
            Toast.makeText(requireContext(), "Местоположение выбрано", Toast.LENGTH_SHORT).show()
        }

        parentFragmentManager.setFragmentResultListener("users_selected", viewLifecycleOwner) { _, result ->
            val selectedUsers = result.getParcelableArray("selected_users") as? Array<ru.netology.nework.model.User>
            if (!selectedUsers.isNullOrEmpty()) {
                binding.selectMentionsButton.text = "Выбрано: ${selectedUsers.size}"
                Toast.makeText(requireContext(), "Выбрано пользователей: ${selectedUsers.size}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_post_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_post -> {
                savePost()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun savePost() {
        val content = binding.content.text.toString().trim()
        
        if (content.isBlank()) {
            binding.contentInput.error = "Содержимое поста не может быть пустым"
            return
        }

        val link = binding.link.text.toString().trim()

        val linkToSave = if (link.isBlank()) null else link

        viewModel.createPost(content, linkToSave, selectedAttachment, selectedCoordinates)
    }

                   private fun handleImageSelection(uri: Uri) {
                   if (!MediaUtils.validateFileSize(requireContext(), uri)) {
                       Toast.makeText(requireContext(), "Размер файла превышает 15MB", Toast.LENGTH_SHORT).show()
                       return
                   }

                   val file = MediaUtils.copyFileToCache(requireContext(), uri)
                   if (file != null) {


                       Glide.with(binding.attachmentPreview)
                           .load(uri)
                           .into(binding.attachmentPreview)
                       binding.attachmentPreview.visibility = View.VISIBLE

                       viewLifecycleOwner.lifecycleScope.launch {
                           viewModel.uploadFile(file)
                               .onSuccess { attachment ->
                                   selectedAttachment = attachment
                                   Toast.makeText(requireContext(), "Файл загружен успешно", Toast.LENGTH_SHORT).show()
                               }
                               .onFailure { exception ->
                                   Toast.makeText(requireContext(), "Ошибка загрузки файла: ${exception.message}", Toast.LENGTH_SHORT).show()
                                   binding.attachmentPreview.visibility = View.GONE
                               }
                       }
                   } else {
                       Toast.makeText(requireContext(), "Ошибка при обработке изображения", Toast.LENGTH_SHORT).show()
                   }
               }

               override fun onDestroyView() {

                   super.onDestroyView()
                   _binding = null
               }
}
