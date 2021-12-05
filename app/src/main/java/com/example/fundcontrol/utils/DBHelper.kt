package com.example.fundcontrol.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fundcontrol.Transaction
import kotlin.collections.ArrayList

class DBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {

    companion object {
        private const val DATABASE_NAME = "FundControl.db"
        private const val DATABASE_VER = 1
        // Table
        private const val TABLE_NAME = "Transactions"
        private const val COL_ID = "Id"
        private const val COL_DATE = "Date"
        private const val COL_VALUE = "Value"
        private const val COL_CATEGORY = "Category"
        private const val COL_NOTE = "Note"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY," +
                " $COL_DATE TEXT," +
                " $COL_VALUE INTEGER," +
                " $COL_CATEGORY TEXT," +
                " $COL_NOTE TEXT)"
        db!!.execSQL(query)

        initData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }


    val allTransactions: List<Transaction>
        get() {
            val lstTransactions = ArrayList<Transaction>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                do {
                    val transaction = Transaction(
                        cursor.getInt(cursor.getColumnIndex(COL_ID)),
                        cursor.getString(cursor.getColumnIndex(COL_DATE)),
                        cursor.getString(cursor.getColumnIndex(COL_CATEGORY)),
                        cursor.getInt(cursor.getColumnIndex(COL_VALUE)),
                        cursor.getString(cursor.getColumnIndex(COL_NOTE))
                    )

                    lstTransactions.add(transaction)
                } while (cursor.moveToNext())
            }
            db.close()
            return lstTransactions
        }


    fun addTransaction(transaction: Transaction) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID, transaction.id)
        values.put(COL_DATE, transaction.date)
        values.put(COL_VALUE, transaction.value)
        values.put(COL_CATEGORY, transaction.category)
        values.put(COL_NOTE, transaction.note)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }


    fun deleteTransaction(transaction: Transaction) {
        val db = this.writableDatabase

        db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(transaction.id.toString()))
        db.close()
    }

    fun getNextId(): Int {
        return allTransactions.size
    }

    private fun initData(db: SQLiteDatabase) {
        listOf(
            Transaction(0,"23 ноября, 2021", "еда", -300, "KFC"),
            Transaction(1,"23 ноября, 2021", "другие", 50000, "Зарплата"),
            Transaction(2,"23 ноября, 2021", "подарки", -2000, "KFC"),
            Transaction(3,"23 ноября, 2021", "транспорт", -650, "Электричка"),
            Transaction(4,"23 ноября, 2021", "транспорт", -50, "Автобус"),
            Transaction(5,"24 ноября, 2021", "еда", -900, "Суши"),
            Transaction(6,"24 ноября, 2021", "face palm", -5000, "Проиграла спор 5000р"),
            Transaction(7,"25 ноября, 2021", "другие", 5000, "Выиграла спор"),
            Transaction(8,"25 ноября, 2021", "путешествия", -25000, "Сгоняла в Питер")
        ).forEach {
            val values = ContentValues()
            values.put(COL_ID, it.id)
            values.put(COL_DATE, it.date)
            values.put(COL_VALUE, it.value)
            values.put(COL_CATEGORY, it.category)
            values.put(COL_NOTE, it.note)

            db.insert(TABLE_NAME, null, values)
        }


    }
}