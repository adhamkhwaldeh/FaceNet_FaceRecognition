package com.aljawad.sons.verifoxxcore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aljawad.sons.mainlibrary.extensions.observe
import com.aljawad.sons.verifoxxcore.databinding.FragmentHomeBinding
import com.aljawad.sons.verifoxxcore.viewModel.ImageProcessingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    val viewModel: ImageProcessingViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.loggedUser) {
            binding.loggedUserTxt.text = it
        }

    }

}