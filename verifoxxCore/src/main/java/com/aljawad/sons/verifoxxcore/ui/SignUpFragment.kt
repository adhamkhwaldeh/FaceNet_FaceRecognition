package com.aljawad.sons.verifoxxcore.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aljawad.sons.mainlibrary.dialogs.ProgressDialog
import com.aljawad.sons.mainlibrary.states.BaseState
import com.aljawad.sons.mainlibrary.states.PoJoByIdStateLoadedSuccessfully
import com.aljawad.sons.verifoxxcore.R
import com.aljawad.sons.verifoxxcore.databinding.FragmentSignupBinding
import com.aljawad.sons.verifoxxcore.viewModel.ImageProcessingViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    val viewModel: ImageProcessingViewModel by activityViewModels()

    private lateinit var directoryAccessLauncher: ActivityResultLauncher<Intent>

    private lateinit var binding: FragmentSignupBinding

    private fun launchChooseDirectoryIntent() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        // startForActivityResult is deprecated.
        // See this SO thread -> https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
        directoryAccessLauncher.launch(intent)
    }

    private fun onUserAdded() {
        lifecycleScope.launchWhenResumed {
            viewModel.userAddFlow.collect {
                when (it) {
                    is BaseState.Loading -> {
//                        progressDialog.showDialog(childFragmentManager)
                    }
                    is BaseState.LoadingDismiss -> {
                        ProgressDialog.closeDialog(childFragmentManager)
                    }
                    is BaseState.InternalServerError -> {
//                        Snackbar.make(
//                            binding.root,
//                            it.message ?: getText(R.string.unexpectedErrorHappened),
//                            Snackbar.LENGTH_LONG
//                        ).show()
                    }
                    is BaseState.NoAuthorized -> {
//                        Snackbar.make(
//                            binding.root,
//                            getText(R.string.unAuthorized),
//                            Snackbar.LENGTH_LONG
//                        ).show()
                    }
                    is BaseState.NoInternetError -> {
//                        Snackbar.make(
//                            binding.root,
//                            getText(R.string.checkYourInternetConnection),
//                            Snackbar.LENGTH_LONG
//                        ).show()
                    }
                    is BaseState.Conflict -> {
//                        Snackbar.make(
//                            binding.root,
//                            it.message ?: getText(R.string.dataIssue),
//                            Snackbar.LENGTH_LONG
//                        ).show()
                    }
                    is BaseState.NotDataFound -> {
                        Snackbar.make(
                            binding.root,
                            getText(R.string.weCouldNotDetectTheFace),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is PoJoByIdStateLoadedSuccessfully -> {
                        viewModel.faceList.value = it.data?.first
                        findNavController().navigate(R.id.action_SignUpFragment_to_SignInFragment)
                        Log.v("", "")
                    }
                    else -> {

                    }
                }

            }
        }
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {

            } else {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Camera Permission")
                    setMessage("The app couldn't function without the camera permission.")
                    setCancelable(false)
                    setPositiveButton("ALLOW") { dialog, which ->
                        dialog.dismiss()
                        requestCameraPermission()
                    }
                    setNegativeButton("CLOSE") { dialog, which ->
                        dialog.dismiss()
                        activity?.finish()
                    }
                    create()
                }
                alertDialog.show()
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
        }

        directoryAccessLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val bitmap =
                    it.data?.extras?.get("data") as Bitmap? ?: return@registerForActivityResult
                val images = ArrayList<Pair<String, Bitmap>>()
                images.add(Pair(binding.fullNameEditText.text.toString(), bitmap))
                viewModel.addUser(images)
                onUserAdded()
            }


        binding.signUpBtn.setOnClickListener {
            if (binding.fullNameEditText.text.isNullOrBlank()) {
                binding.fullNameEditText.error = getString(R.string.pleaseEnterFullName)
                binding.fullNameEditText.requestFocus()
            } else {
                launchChooseDirectoryIntent()
            }
        }

        binding.signInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_SignUpFragment_to_SignInFragment)
        }

//        observe(viewModel.faceList) {
//            findNavController().navigate(R.id.action_SignUpFragment_to_SignInFragment)
//            Log.v("", "")
//        }

    }


}