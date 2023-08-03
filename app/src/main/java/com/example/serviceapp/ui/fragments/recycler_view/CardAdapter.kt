package com.example.serviceapp.ui.fragments.recycler_view

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.serviceapp.R
import com.example.serviceapp.databinding.RecyclerViewElementBinding
import com.example.serviceapp.ui.fragments.MainFragmentDirections
import com.example.serviceapp.ui.fragments.models.UserModel

class CardAdapter(
    private val navController: NavController,
    private val userData: UserModel.UserData
) :
    RecyclerView.Adapter<CardAdapter.CardHolder>() {
    private val cardList = ArrayList<Card>()

    class CardHolder(
        item: View,
        private val navController: NavController,
        private val userData: UserModel.UserData
    ) :
        RecyclerView.ViewHolder(item) {

        private val binding = RecyclerViewElementBinding.bind(item)

        fun bind(card: Card) {
            with(binding) {
                masterLayout.setOnClickListener {
                    clickArrow.animate().rotationBy(180F).setDuration(100).start()
                    createLayoutAnimations()
                }
                makeOrderButton.setOnClickListener {
                    if (!userData.userState)
                        navController.navigate(R.id.login_fragment)
                    else navController.navigate(
                        MainFragmentDirections.actionMainFragmentToAccountFragment(
                            userData.userName
                        )
                    )

                }
                type.text = card.typeOfService
                serviceDescription.text = card.descriptionOfService
                price.text = card.priceOfService.toString()
                time.text = card.time
            }
        }

        private fun createLayoutAnimations() {
            with(binding) {
                createLayoutAnimation(masterDescLayout).layoutTransition.enableTransitionType(
                    LayoutTransition.CHANGING
                )
                createLayoutAnimation(masterExpLayout).layoutTransition.enableTransitionType(
                    LayoutTransition.CHANGING
                )
                createLayoutAnimation(masterRatingLayout).layoutTransition.enableTransitionType(
                    LayoutTransition.CHANGING
                )
            }
        }

        private fun createLayoutAnimation(layout: LinearLayout): LinearLayout {
            val params: ViewGroup.LayoutParams = layout.layoutParams
            if (layout.layoutParams.height == 0)
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            else params.height = 0
            layout.layoutParams = params
            return layout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_element, parent, false)
        return CardHolder(view, navController, userData)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addCard(card: Card) {
        cardList.add(card)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun cleanCards() {
        cardList.clear()
        notifyDataSetChanged()
    }
}