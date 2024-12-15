package com.exchange.convertedcash.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exchange.convertedcash.R
import com.exchange.convertedcash.model.Currency

class CurrencyAdapter(private val currencyList: List<Currency>) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val codeTextView: TextView = itemView.findViewById(R.id.currencyCode)
        val nameTextView: TextView = itemView.findViewById(R.id.currencyName)
        val rateTextView: TextView = itemView.findViewById(R.id.currencyRate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder =
        CurrencyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency_fiat, parent, false)
        )

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = currencyList[position]
        holder.codeTextView.text = currency.code
        holder.nameTextView.text = currency.name ?: currency.code
        holder.rateTextView.text = String.format("%.4f", currency.rate)
    }

    override fun getItemCount(): Int = currencyList.size
}