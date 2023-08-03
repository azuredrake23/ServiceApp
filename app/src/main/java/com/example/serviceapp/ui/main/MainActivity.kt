package com.example.serviceapp.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    private val binding by viewBinding(ActivityMainBinding::bind)

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(binding.maFragmentNavHost.id) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(navController.graph)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        setupToolbar()
        setObservers()
        viewModel.getAppLanguage()
    }

    private fun setObservers() {
        viewModel.appLanguage
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {
                setupLanguage(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun setupLanguage(locales: LocaleListCompat) {
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.maToolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}