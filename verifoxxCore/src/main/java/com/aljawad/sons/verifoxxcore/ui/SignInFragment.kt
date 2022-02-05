package com.aljawad.sons.verifoxxcore.ui

import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.aljawad.sons.dtos.model.FrameAnalyser
import com.aljawad.sons.dtos.utils.Logger
import com.aljawad.sons.mainlibrary.extensions.observe
import com.aljawad.sons.verifoxxcore.R
import com.aljawad.sons.verifoxxcore.databinding.FragmentSigninBinding
import com.aljawad.sons.verifoxxcore.viewModel.ImageProcessingViewModel
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors

@AndroidEntryPoint
class SignInFragment : Fragment() {

    val viewModel: ImageProcessingViewModel by activityViewModels()

    private lateinit var binding: FragmentSigninBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var frameAnalyser: FrameAnalyser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bboxOverlay.setWillNotDraw(false)
        binding.bboxOverlay.setZOrderOnTop(true)
        frameAnalyser =
            FrameAnalyser(requireContext(), binding.bboxOverlay, viewModel.faceNetModel) {
                if (it != "Unknown") {
                    binding.loginAsBtn.text = getString(R.string.signInAs) + " " + it
                    binding.loginAsBtn.tag = it
                } else {
                    binding.loginAsBtn.text = ""
                }
            }

        observe(viewModel.faceList) {
            frameAnalyser.faceList = it
        }

        startCameraPreview()

        binding.loginAsBtn.setOnClickListener {
            if (!binding.loginAsBtn.text.isNullOrBlank()) {
                viewModel.loggedUser.value = binding.loginAsBtn.tag.toString()
                findNavController().navigate(R.id.action_SignInFragment_to_HomeFragment)
            }
        }

    }

    // Attach the camera stream to the PreviewView.
    private fun startCameraPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        val imageFrameAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(480, 640))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageFrameAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), frameAnalyser)
        cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            preview,
            imageFrameAnalysis
        )
    }


}