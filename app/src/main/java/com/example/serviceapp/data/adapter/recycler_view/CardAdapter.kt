package com.example.serviceapp.data.adapter.recycler_view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.serviceapp.R
import com.example.serviceapp.databinding.RecyclerViewElementBinding
import com.example.serviceapp.data.models.UserModel

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
                    createLayoutAnimation()
                }
                makeOrderButton.setOnClickListener {
//                    if (!userData.userState)
//                        navController.navigate(R.id.login_fragment)
//                    else navController.navigate(
//                        MainFragmentDirections.actionMainFragmentToAccountDest(
//                            userData.userName
//                        )
//                    )

                }
                type.text = card.typeOfService
                serviceDescription.text = card.descriptionOfService
                price.text = card.priceOfService.toString()
                time.text = card.time
                master.text = card.master
                masterDescription.text = card.descriptionOfMaster
                experience.text = card.experience.toString()
                rating.text = card.rating.toString()
            }
        }

        private fun createLayoutAnimation() {
            with(binding) {
                if (masterContainer.visibility == View.VISIBLE)
                    masterContainer.visibility = View.GONE
                else masterContainer.visibility = View.VISIBLE
            }
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