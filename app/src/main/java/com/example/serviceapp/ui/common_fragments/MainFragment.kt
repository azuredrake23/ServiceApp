package com.example.serviceapp.ui.common_fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import com.example.serviceapp.databinding.MainFragmentBinding
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
class MainFragment : Fragment(R.layout.main_fragment) {

    private val binding by viewBinding(MainFragmentBinding::bind)
    private val viewModel: MainFragmentViewModel by viewModels()
    private val userViewModel: UserDatabaseViewModel by viewModels()
    private val serviceViewModel: ServiceDatabaseViewModel by viewModels()
    private val masterViewModel: MasterDatabaseViewModel by viewModels()
//    private val firebaseViewModel: FirebaseViewModel by viewModels()

    var isSettingsFragment = false
    var accessStateFragment = false
    private val preferenceManager by lazy {
        PreferenceManager(requireContext())
    }

//    private val firebaseUser by lazy {
//        firebaseViewModel.firebaseUser
//    }

    private lateinit var adapter: CardAdapter

    private val userData = UserModel.UserData()
    private var listOfServices = listOf<Service>()
    private var listOfMasters = listOf<Master>()

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        preferenceManager = PreferenceManager(context)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setPrefs()
        setListeners()
        setupMenu()
        initLayout()
    }

//    override fun onResume() {
//        super.onResume()
//        initLayout()
////        Handler().postDelayed({ initCardAdapter() }, 100)
//    }

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
                    R.id.settings_fragment -> {
                        isSettingsFragment = true
                        findNavController().navigate(
                            R.id.settings_fragment
                        )
                    }

                    R.id.account_fragment -> {
                        findNavController().navigate(
                            R.id.account_fragment
                        )
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initLayout() {
        with(binding) {
            addButton.setOnClickListener {
                findNavController().navigate(R.id.add_service_fragment)

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

    private fun setPrefs() {
        setAccessState()
        setBackstackDisable()
    }

    private fun setBackstackDisable() {
        (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callback
        )
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

}
