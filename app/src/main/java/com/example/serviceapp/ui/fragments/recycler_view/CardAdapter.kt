package com.example.serviceapp.ui.fragments.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.serviceapp.R
import com.example.serviceapp.databinding.RecyclerViewElementBinding

class CardAdapter: RecyclerView.Adapter<CardAdapter.CardHolder>() {
    private val cardList = ArrayList<Card>()
    class CardHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = RecyclerViewElementBinding.bind(item)
        fun bind(card: Card){
            with (binding){
                type.text = card.typeOfService
                description.text = card.descriptionOfService
                price.text = card.priceOfService.toString()
                time.text = card.time
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_element, parent, false)
        return CardHolder(view)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun addCard(card:Card){
        cardList.add(card)
        notifyDataSetChanged()
    }

    fun cleanCards(){
        cardList.clear()
        notifyDataSetChanged()
    }
}