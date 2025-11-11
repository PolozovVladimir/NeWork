package ru.netology.nework.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentRegisterBinding
import ru.netology.nework.util.MediaUtils

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var selectedAvatarUri: Uri? = null

    private val avatarPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedAvatarUri = uri
                Glide.with(binding.avatarPreview)
                    .load(uri)
                    .circleCrop()
                    .into(binding.avatarPreview)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            val login = binding.login.text.toString().trim()
            val name = binding.name.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val passwordRepeat = binding.passwordRepeat.text.toString().trim()

            if (login.isBlank()) {
                binding.loginInput.error = "Логин не может быть пустым"
                return@setOnClickListener
            }

            if (name.isBlank()) {
                binding.nameInput.error = "Имя не может быть пустым"
                return@setOnClickListener
            }

            if (password.isBlank()) {
                binding.passwordInput.error = "Пароль не может быть пустым"
                return@setOnClickListener
            }

            if (password != passwordRepeat) {
                binding.passwordRepeatInput.error = "Пароли не совпадают"
                return@setOnClickListener
            }

            viewModel.register(login, password, name, selectedAvatarUri)
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.selectAvatarButton.setOnClickListener {
            val intent = MediaUtils.createImagePickerIntent()
            avatarPickerLauncher.launch(intent)
        }

        viewModel.authState.observe(viewLifecycleOwner) { authState ->
            if (authState.token != null) {
                findNavController().navigate(R.id.action_registerFragment_to_postsFragment)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




