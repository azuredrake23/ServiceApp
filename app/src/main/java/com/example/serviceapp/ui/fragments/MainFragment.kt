package com.example.serviceapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.Service
import com.example.serviceapp.data.common.utils.PreferenceManager
import com.example.serviceapp.databinding.MainFragmentBinding
import com.example.serviceapp.ui.fragments.database_view_models.ServiceDatabaseViewModel
import com.example.serviceapp.ui.fragments.recycler_view.Card
import com.example.serviceapp.ui.fragments.recycler_view.CardAdapter
import com.example.serviceapp.ui.main.MainViewModel
import com.example.serviceapp.ui.utils.mappers.Access
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment) {

    private val binding by viewBinding(MainFragmentBinding::bind)
    private val viewModel: MainFragmentViewModel by activityViewModels()
    private val viewModelService: ServiceDatabaseViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    var isSettingsFragment = false
    var accessStateFragment = false
    private lateinit var preferenceManager: PreferenceManager
    private val adapter by lazy {
        CardAdapter()
    }
    private var listOfServices = listOf<Service>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferenceManager = PreferenceManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setPrefs()
        setListeners()
        setupMenu()
    }

    override fun onResume() {
        super.onResume()
        initLayout()
        Handler().postDelayed({ addCard() }, 100)
    }

    private fun addCard() {
        with(binding) {
            recyclerView.layoutManager = GridLayoutManager(this@MainFragment.requireContext(), 3)
            recyclerView.adapter = adapter
            Log.d("MAIN FRAGMENT", "services: $listOfServices")
            adapter.cleanCards()
            for (i in listOfServices.indices) {
                adapter.addCard(
                    Card(
                        i,
                        listOfServices[i].service_type,
                        listOfServices[i].description,
                        listOfServices[i].price,
                        listOfServices[i].time
                    )
                )
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.settings_dest -> {
                        isSettingsFragment = true
                        menuItem.onNavDestinationSelected(findNavController())
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initLayout() {
        with(binding) {
            addButton.setOnClickListener {
                findNavController().navigate(R.id.add_employee_fragment)

//                val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
//
//                if (viewTypeMode == ViewState.Compass) {
//                    transaction.replace(com.google.android.gms.location.R.id.container_id, compassFragmentObj)
//                } else if (viewTypeMode == ViewState.Map) {
//                    transaction.replace(com.google.android.gms.location.R.id.container_id, mapsFragmentObj)
//                }
//                transaction.commit()
            }
            if (accessStateFragment) addButton.visibility = View.VISIBLE
            else addButton.visibility = View.INVISIBLE
        }

        isSettingsFragment = false
    }

    private fun setObservers() {
        with(viewModel) {
            accessState
                .flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
                .onEach {
                    accessStateFragment = it
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }

        with(mainViewModel) {

        }

        with(viewModelService) {
            allServices
                .flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
                .onEach {
                    listOfServices = it
                    Log.d(
                        "MAIN FRAGMENT FROM OBSERVE",
                        "services: $it"
                    )
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun setListeners() {

    }

    private fun setPrefs() {
        setAccessState()
    }

    private fun setAccessState() {
        if (preferenceManager.get(
                resources.getString(R.string.access),
                "USER"
            ) == Access.ADMIN.name
        )
            viewModel.setAccessState(true)
        else if (preferenceManager.get(
                resources.getString(R.string.access),
                "USER"
            ) == Access.USER.name
        )
            viewModel.setAccessState(false)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}
