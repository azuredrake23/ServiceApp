package com.example.serviceapp.ui.firebase_fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.RegisterFragmentBinding

class RegisterFragment: Fragment(R.layout.register_fragment) {
    val binding by viewBinding(RegisterFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}