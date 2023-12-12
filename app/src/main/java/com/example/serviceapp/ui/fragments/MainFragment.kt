package com.example.serviceapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.MainFragmentBinding
import com.example.serviceapp.ui.fragments.common_fragments.AnnouncementFragment
import com.example.serviceapp.ui.fragments.common_fragments.SubmitFragment
import com.example.serviceapp.ui.fragments.common_fragments.AccountFragment
import com.example.serviceapp.ui.fragments.common_fragments.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment) {

    private val binding by viewBinding(MainFragmentBinding::bind)

    private val announcementFragment by lazy {
        AnnouncementFragment()
    }

    private val submitFragment by lazy {
        SubmitFragment()
    }

    private val accountFragment by lazy {
        AccountFragment()
    }

    private val settingsFragment by lazy {
        SettingsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomMenu()
        setBackstackDisable()
    }

    private fun setupBottomMenu() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.announcement -> {
                    transactionAction(announcementFragment)
                }

                R.id.submit -> {
                    transactionAction(submitFragment)
                }

                R.id.account -> {
                    transactionAction(accountFragment)
                }

                R.id.settings -> {
                    transactionAction(settingsFragment)
                }
            }
            true
        }
    }

    private fun transactionAction(fragment: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragmentContainerView, fragment).commit()
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
}
