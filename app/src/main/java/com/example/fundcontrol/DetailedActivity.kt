package com.example.fundcontrol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fundcontrol.utils.DBHelper
import kotlinx.android.synthetic.main.activity_detailed.*

class DetailedActivity : AppCompatActivity() {

    internal lateinit var db: DBHelper
    private lateinit var transactions: List<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) // убираем иконку батареи, сети, времени и прочего...
        setContentView(R.layout.activity_detailed)

        db = DBHelper(this@DetailedActivity) // создаем экземпляр, отвечающий за взаимодействие с БД
        transactions = db.allTransactions // все транзакции, полученные из бд
        configureRecyclerView(transactions) // настройка RecyclerView


        toggle.setOnCheckedChangeListener { group, checkedId -> // логика работы с тройной кнопков фильтра (все/траты/доходы)
            run {
                if (checkedId == sw_filter_all.id) {
                    configureRecyclerView(transactions)
                } else if (checkedId == sw_filter_expense.id) {
                    configureRecyclerView(transactions.filter { it.value < 0 })
                } else {
                    configureRecyclerView(transactions.filter { it.value > 0 })
                }
            }
        }
    }


    private fun configureRecyclerView(transactions: List<Transaction>): Unit {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@DetailedActivity) // последовательное отображение сверху вниз
        rv_transactions.layoutManager = layoutManager
        rv_transactions.setHasFixedSize(true)
        rv_transactions.adapter =
            TransactionsAdapter(transactions, object : TransactionsAdapter.Callback {
                override fun onItemClicked(item: Transaction) {

                }

                override fun onDelete(item: Transaction) {

                    db.deleteTransaction(item) // удаление транзакции из БД

                    this@DetailedActivity.transactions =
                        db.allTransactions // пересоздание RecyclerView, т.к. кол-во транзакций уменьшилось
                    if (sw_filter_income.isChecked) {
                        configureRecyclerView(this@DetailedActivity.transactions.filter { it.value > 0 })
                    } else if (sw_filter_expense.isChecked) {
                        configureRecyclerView(this@DetailedActivity.transactions.filter { it.value < 0 })
                    } else {
                        configureRecyclerView(this@DetailedActivity.transactions)
                    }

                }
            })
    }


}
