package com.example.serviceapp.ui.fragments.common_fragments

import androidx.fragment.app.Fragment
import com.example.serviceapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnnouncementFragment : Fragment(R.layout.announcement_fragment) {

//    private val binding by viewBinding(AnnouncementFragmentBinding::bind)
//
//    private val viewModel: MainFragmentViewModel by viewModels()
//    private val serviceViewModel: ServiceDatabaseViewModel by viewModels()
//    private val masterViewModel: MasterDatabaseViewModel by viewModels()
//
//    private lateinit var adapter: CardAdapter
//
//    private val userData = UserModel.UserData()
//    private var listOfServices = listOf<Service>()
//    private var listOfMasters = listOf<Master>()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initFragment()
//    }
//
//    private fun initFragment() {
//        setObservers()
//        setListeners()
//    }
//
//    private fun initCardAdapter() {
//        with(binding) {
//            recyclerView.layoutManager = GridLayoutManager(context, 1)
//            adapter = CardAdapter(findNavController(), userData)
//            recyclerView.adapter = adapter
//            adapter.cleanCards()
//            for (i in listOfServices.indices) {
//                adapter.addCard(
//                    Card(
//                        listOfServices[i].service_type,
//                        listOfServices[i].description,
//                        listOfServices[i].price,
//                        listOfServices[i].time,
//                        listOfMasters[i].master,
//                        listOfMasters[i].description,
//                        listOfMasters[i].experience,
//                        listOfMasters[i].rating,
//                    )
//                )
//            }
//        }
//    }
//
//    private fun setObservers() {
//        with(viewModel) {
//
//        }
//
//        with(serviceViewModel) {
//            allServices
//                .flowWithLifecycle(
//                    viewLifecycleOwner.lifecycle,
//                    Lifecycle.State.STARTED
//                )
//                .onEach {
//                    listOfServices = it
//                }
//                .launchIn(viewLifecycleOwner.lifecycleScope)
//        }
//
//        with(masterViewModel) {
//            allMasters
//                .flowWithLifecycle(
//                    viewLifecycleOwner.lifecycle,
//                    Lifecycle.State.STARTED
//                )
//                .onEach {
//                    listOfMasters = it
//                }
//                .launchIn(viewLifecycleOwner.lifecycleScope)
//        }
//    }
//
//    private fun setListeners() {
//
//    }

}