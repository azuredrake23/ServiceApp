package com.example.serviceapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.ui.fragments.database_view_models.UserDatabaseViewModel
import kotlinx.coroutines.launch

class AccountFragment : Fragment(R.layout.account_fragment) {

    private val binding by viewBinding(AccountFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()
    private val args: AccountFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObservers()
    }

    private fun setObservers() {
        with (userViewModel){

        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        with(binding){
            tvHi.text = "Hi, ${args.userName}"
        }
    }
}