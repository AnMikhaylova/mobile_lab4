package com.example.fundcontrol

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.fundcontrol.utils.DBHelper
import kotlinx.android.synthetic.main.activity_create_transaction.*
import java.lang.Exception
import java.util.*
import kotlin.math.abs


class CreateTransactionActivity : AppCompatActivity() {


    internal lateinit var db: DBHelper
    private val categories = arrayOf(
        "другие",
        "еда",
        "транспорт",
        "здоровье",
        "путешествия",
        "подарки",
        "отдых",
        "face palm"
    ) // список категорий транзакций

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) // убираем иконки батареи, часов, сети и прочее
        setContentView(R.layout.activity_create_transaction)

        db = DBHelper(this@CreateTransactionActivity) // подключили БД
        initRouting() // инициализировали роутинг
        initCalendar() // логика календаря
        initSpinner() // логика работы выпадающего списка с категориями транзакций
    }

    private fun getMonthName(id: Int): String {

        return when (id) {
            0 -> "января"
            1 -> "февраля"
            2 -> "марта"
            3 -> "апреля"
            4 -> "мая"
            5 -> "июня"
            6 -> "июля"
            7 -> "августа"
            8 -> "сентября"
            9 -> "октября"
            10 -> "ноября"
            11 -> "декабря"

            else -> "undefined"
        }
    }

    private fun getToday(): String {
        val date = Date()
        return "" + date.date + " " + getMonthName(date.month) + ", " + (date.year + 1900)
    }

    private fun initRouting() {
        btn_cancel.setOnClickListener {
            finish()
        }

        btn_save.setOnClickListener {

            try {
                val id = db.getNextId()
                val date = et_calendar.text.toString()
                val category = sp_category_chooser.selectedItem.toString()
                val note = et_note.text.toString()
                var value = et_value.text.toString().toInt()
                if (sw_create_expense.isChecked) value =
                    -abs(value); // если выбран флаг "траты", то значение транзакций идет в бд со знаком минус
                val transaction = Transaction(id, date, category, value, note)
                db.addTransaction(transaction) // транзакция добавляется в БД
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.create_error), Toast.LENGTH_SHORT)
                    .show() // в случае булщита всплывает сообщение с ошибкой
            }
        }

    }

    private fun initSpinner() { // настройка выпадающего списка выбора категории при создании транзакции
        val adapter = ArrayAdapter<String>(
            this@CreateTransactionActivity,
            R.layout.item,
            categories
        )
        adapter.setDropDownViewResource(R.layout.item)
        sp_category_chooser.adapter = adapter
        sp_category_chooser.setSelection(0)
        sp_category_chooser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                sp_category_chooser.setSelection(0)
            }
        }
    }

    private fun initCalendar() {
        et_calendar.text = getToday() // изначально ставим дату сегодня
        et_calendar.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    et_calendar.text =
                        "" + dayOfMonth + " " + getMonthName(monthOfYear) + ", " + year
                },
                year,
                month,
                day
            ).also { it.show() }
        }
    }
}
