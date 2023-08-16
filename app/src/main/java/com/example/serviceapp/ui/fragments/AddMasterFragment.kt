package com.example.serviceapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.Master
import com.example.serviceapp.data.entities.Errors
import com.example.serviceapp.databinding.AddMasterFragmentBinding
import com.example.serviceapp.ui.fragments.database_view_models.MasterDatabaseViewModel

class AddMasterFragment: Fragment(R.layout.add_master_fragment) {
    private val binding by viewBinding(AddMasterFragmentBinding::bind)
    private val masterViewModel: MasterDatabaseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        with(masterViewModel){

        }
    }

    private fun setListeners() {
        with (binding){
            addMasterButton.setOnClickListener {
                if (textMaster.text.isEmpty() || textMasterDescription.text.isEmpty() || textExperience.text.isEmpty() || textRating.text.isEmpty()) {
                    Toast.makeText(context, Errors.EMPTY_FIELDS, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                masterViewModel.insert(
                    Master(
                        master = textMaster.text.toString(),
                        description = textMasterDescription.text.toString(),
                        experience = textExperience.text.toString().toDouble(),
                        rating = textRating.text.toString().toDouble()
                    )
                )

                findNavController().navigate(R.id.main_fragment)
            }
        }
    }
}