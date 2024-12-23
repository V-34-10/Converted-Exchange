package com.exchange.convertedcash.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exchange.convertedcash.R
import com.exchange.convertedcash.model.Cryptocurrency

class CryptocurrencyAdapter(private val cryptoList: List<Cryptocurrency>) :
    RecyclerView.Adapter<CryptocurrencyAdapter.CryptocurrencyViewHolder>() {

    class CryptocurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name_currency)
        val nameShortTextView: TextView = itemView.findViewById(R.id.short_name_currency)
        val priceTextView: TextView = itemView.findViewById(R.id.price_currency)
        val imageCurrency: ImageView = itemView.findViewById(R.id.image_currency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptocurrencyViewHolder =
        CryptocurrencyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
        )

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onBindViewHolder(holder: CryptocurrencyViewHolder, position: Int) {
        val crypto = cryptoList[position]

        val newName = crypto.name.replace("(${crypto.symbol})", "", ignoreCase = true)

        holder.nameTextView.text = newName
        holder.nameShortTextView.text = crypto.symbol
        holder.priceTextView.text = String.format("$%.5f", crypto.usdPrice)

        val imageResource = when (crypto.symbol.uppercase()) {
            "BTC" -> R.drawable.bitcoin_logo
            "ETH" -> R.drawable.ethereum_ico
            "BNB" -> R.drawable.bnb_logo
            "PEPE" -> R.drawable.pepe_ico
            "SOL" -> R.drawable.sol_logo
            "DOGE" -> R.drawable.doge_logo
            "WIF" -> R.drawable.wif_logo
            "XRP" -> R.drawable.xrp_logo
            "TON" -> R.drawable.ton_logo
            "LTC" -> R.drawable.ltc_logo
            else -> {
                R.drawable.bitcoin_logo
            }
        }
        holder.imageCurrency.setImageResource(imageResource)
    }

    override fun getItemCount(): Int = cryptoList.size
}