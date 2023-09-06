package com.example.serviceapp.ui.common_fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.Service
import com.example.serviceapp.data.entities.Errors
import com.example.serviceapp.databinding.AddServiceFragmentBinding
import com.example.serviceapp.ui.view_models.database_view_models.ServiceDatabaseViewModel

class AddServiceFragment: Fragment(R.layout.add_service_fragment) {
    private val binding by viewBinding(AddServiceFragmentBinding::bind)
    private val serviceViewModel: ServiceDatabaseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
    }

    private fun setObservers() {

    }

    private fun setListeners() {
        with (binding){
            addServiceButton.setOnClickListener {
                if (textType.text.isEmpty() || textServiceDescription.text.isEmpty() || textPrice.text.isEmpty() || textTime.text.isEmpty()) {
                    Toast.makeText(context, Errors.EMPTY_FIELDS, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                serviceViewModel.insert(
                    Service(
                        service_type = textType.text.toString(),
                        description = textServiceDescription.text.toString(),
                        price = textPrice.text.toString().toDouble(),
                        time = textTime.text.toString()
                    )
                )

                findNavController().navigate(R.id.main_fragment)
            }

        }
    }
}