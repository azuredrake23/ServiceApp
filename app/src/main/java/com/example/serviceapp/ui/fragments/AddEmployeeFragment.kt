package com.example.serviceapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.Service
import com.example.serviceapp.databinding.AddEmployeeFragmentBinding
import com.example.serviceapp.ui.fragments.database_view_models.ServiceDatabaseViewModel

class AddEmployeeFragment : Fragment(R.layout.add_employee_fragment) {
    private val binding by viewBinding(AddEmployeeFragmentBinding::bind)

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
                viewModelService.insert(Service(null, textType.text.toString(), textDescription.text.toString(), textPrice.text.toString().toDouble(), textTime.text.toString()))
                findNavController().navigate(R.id.main_fragment)
            }
        }
    }
}