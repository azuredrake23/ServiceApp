package com.example.serviceapp.ui.common_fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.SubmitFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubmitFragment : Fragment(R.layout.submit_fragment) {
    private val binding by viewBinding(SubmitFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragment()
    }

    private fun initFragment() {

    }
}