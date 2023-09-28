package com.example.serviceapp.ui.common_fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.Master
import com.example.serviceapp.data.common.database.entities.Service
import com.example.serviceapp.data.common.utils.PreferenceManager
import com.example.serviceapp.databinding.AnnouncementFragmentBinding
import com.example.serviceapp.ui.common_fragments.models.UserModel
import com.example.serviceapp.ui.common_fragments.recycler_view.Card
import com.example.serviceapp.ui.common_fragments.recycler_view.CardAdapter
import com.example.serviceapp.ui.view_models.MainFragmentViewModel
import com.example.serviceapp.ui.view_models.database_view_models.MasterDatabaseViewModel
import com.example.serviceapp.ui.view_models.database_view_models.ServiceDatabaseViewModel
import com.example.serviceapp.ui.view_models.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.utils.mappers.Access
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AnnouncementFragment : Fragment(R.layout.announcement_fragment) {

    private val binding by viewBinding(AnnouncementFragmentBinding::bind)

    private val viewModel: MainFragmentViewModel by viewModels()
    private val userViewModel: UserDatabaseViewModel by viewModels()
    private val serviceViewModel: ServiceDatabaseViewModel by viewModels()
    private val masterViewModel: MasterDatabaseViewModel by viewModels()

    private lateinit var adapter: CardAdapter

    private val userData = UserModel.UserData()
    private var listOfServices = listOf<Service>()
    private var listOfMasters = listOf<Master>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        setObservers()
        setListeners()
    }

    private fun initCardAdapter() {
        with(binding) {
            recyclerView.layoutManager = GridLayoutManager(context, 1)
            adapter = CardAdapter(findNavController(), userData)
            recyclerView.adapter = adapter
            adapter.cleanCards()
            for (i in listOfServices.indices) {
                adapter.addCard(
                    Card(
                        listOfServices[i].service_type,
                        listOfServices[i].description,
                        listOfServices[i].price,
                        listOfServices[i].time,
                        listOfMasters[i].master,
                        listOfMasters[i].description,
                        listOfMasters[i].experience,
                        listOfMasters[i].rating,
                    )
                )
            }
        }
    }

    private fun setObservers() {
        with(viewModel) {

        }

        with(serviceViewModel) {
            allServices
                .flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
                .onEach {
                    listOfServices = it
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }

        with(masterViewModel) {
            allMasters
                .flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
                .onEach {
                    listOfMasters = it
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun setListeners() {

    }

}