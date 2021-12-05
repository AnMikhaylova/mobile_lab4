package com.example.fundcontrol

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


// Класс для кооректной работы RecyclerView
class TransactionsAdapter(var items: List<Transaction>, val callback: Callback) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TransactionViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_item, parent, false)
    )

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size


    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val date = itemView.findViewById<TextView>(R.id.tv_date)
        private val category = itemView.findViewById<TextView>(R.id.tv_category)
        private val value = itemView.findViewById<TextView>(R.id.tv_value)
        private val note = itemView.findViewById<TextView>(R.id.tv_note)
        private val btnDelete = itemView.findViewById<Button>(R.id.b_delete)


        fun bind(transaction: Transaction) {
            if (transaction.value > 0) value.setTextColor(
                Color.rgb(145,192,168))// если доход, то текст зеленый
            else value.setTextColor(Color.rgb(223, 168, 170)) // если затраты, то текст красный

            date.text = transaction.date
            category.text = transaction.category
            value.text =
                abs(transaction.value).toString() + "\u20BD" // добавляем символ валюты рубля
            note.text = transaction.note

            btnDelete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onDelete(transaction) // когда кликаем "удалить" вызвывается функция обратного вызова
            }


            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(transaction)
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: Transaction)
        fun onDelete(item: Transaction)
    }


    fun updateList(newList: List<Transaction>): Unit {
        items = newList
        notifyDataSetChanged()
    }


}