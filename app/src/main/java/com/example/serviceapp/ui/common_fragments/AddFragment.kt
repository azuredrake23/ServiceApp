package com.example.serviceapp.ui.common_fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.AddFragmentBinding
import com.example.serviceapp.ui.view_models.database_view_models.MasterDatabaseViewModel
import com.example.serviceapp.ui.view_models.database_view_models.ServiceDatabaseViewModel
import com.google.android.material.tabs.TabLayout

class AddFragment : Fragment(R.layout.add_fragment) {
    private val binding by viewBinding(AddFragmentBinding::bind)

//    private val viewModelUser: UserDatabaseViewModel by activityViewModels()
    private val viewModelService: ServiceDatabaseViewModel by activityViewModels()
    private val masterViewModel: MasterDatabaseViewModel by activityViewModels()
private val fragList = listOf(
    AddServiceFragment(),
    AddMasterFragment()
)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
        initView()
    }

    private fun initView() {
        childFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragList[0]).commit()
    }

    private fun setObservers() {

    }

    private fun setListeners(){
        setButtonsListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun setButtonsListeners() {
        with(binding){

            tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    childFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragList[tab?.position!!]).commit()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    childFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragList[tab?.position!!]).commit()
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    childFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragList[tab?.position!!]).commit()
                }
            })
        }
    }
}