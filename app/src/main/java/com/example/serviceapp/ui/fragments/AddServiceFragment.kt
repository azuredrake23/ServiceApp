package com.example.serviceapp.ui.fragments

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
import com.example.serviceapp.ui.fragments.database_view_models.ServiceDatabaseViewModel

class AddServiceFragment : Fragment(R.layout.add_service_fragment) {
    private val binding by viewBinding(AddServiceFragmentBinding::bind)

//    private val viewModelUser: UserDatabaseViewModel by activityViewModels()
    private val viewModelService: ServiceDatabaseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
    }

    private fun setObservers() {

    }

    private fun setListeners(){
        setButtonsListeners()
    }

    private fun setButtonsListeners() {
        with(binding){
            addCardButton.setOnClickListener {
//                val card = Card(textEmployeeID.text.toString().toInt(), textType.text.toString(), textDescription.text.toString(), textPrice.text.toString(), textTime.text.toString())
                if (textType.text.isEmpty() || textDescription.text.isEmpty() || textPrice.text.isEmpty() || textTime.text.isEmpty()) {
                    Toast.makeText(context, Errors.EMPTY_FIELDS, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModelService.insertAll(
                    Service(
                        service_type = textType.text.toString(),
                        description = textDescription.text.toString(),
                        price = textPrice.text.toString().toDouble(),
                        time = textTime.text.toString()
                    )
                )

                findNavController().navigate(R.id.main_fragment)
            }
        }
    }
}