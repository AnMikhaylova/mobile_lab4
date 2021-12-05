package com.example.fundcontrol

import android.app.ActionBar
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.balram.locker.view.LockActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import com.example.cinemahelper.utils.LocaleChecker
import com.example.fundcontrol.utils.DBHelper
import java.util.*


class MainActivity : LockActivity() {

    internal lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale() // загрузка выбранного языка (русский/ангилйский)
        window.setFlags( // убираем иконки часов, батареи, сети и т.п.
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        db = DBHelper(this@MainActivity) // экземпляр класса для общения с SQLite

        configChangeLanguage() //
        l_settings.visibility = View.GONE // изначально блок настроек скрыт
        loadRouting() // инициализируем роутинг для всех нажатий по различным кнопкам
        refreshData() // обновление данных
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }


    private fun refreshData(){
        val transactions = db.allTransactions // обращение к БД (взятие всех транзакций)
        val total = transactions.map { it.value }.sum() // итоговая сумма
        val expenditures = transactions.filter { it.value < 0 }.map { it.value }.sum() // суммарные затраты
        val revenue = transactions.filter { it.value > 0 }.map { it.value }.sum() // суммарные расходы

        tv_total.text = total.toString() + "\u20BD" // добавляем символ валюты рубля
        tv_expenditures.text = expenditures.toString() +"\u20BD"
        tv_revenue.text = revenue.toString() +"\u20BD"
    }



    private fun loadRouting(): Unit {

        btn_add_transaction.setOnClickListener {
            Intent(this@MainActivity, CreateTransactionActivity::class.java).also {
                startActivity(it)
            }
        }

        btn_detailed.setOnClickListener {
            Intent(this@MainActivity, DetailedActivity::class.java).also {
                startActivity(it)
            }
        }

        btn_settings.setOnClickListener {
            btn_settings.visibility = View.GONE
            expand(l_settings)
        }

        tv_settings.setOnClickListener {
            collapse(l_settings)
            btn_settings.visibility = View.VISIBLE
        }


    }

    // Код ниже отвечает за красивую анимацию скрытия и появления блока настроек (при клике на соответствующую кнопку)
    private fun expand(v: View) { // анимированное появление
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight


        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    ActionBar.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }


        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    private fun collapse(v: View) { // анимированное исчезновение
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }


        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }


    private fun configChangeLanguage(): Unit {
        val currentLocale = ConfigurationCompat.getLocales(resources.configuration)[0]
        if(currentLocale.language == "ru"){
            iv_language_icon.setImageResource(R.drawable.ru)  // изменение иконок флага
        } else {
            iv_language_icon.setImageResource(R.drawable.us) // изменение иконок флага
        }

        iv_language_icon.setOnClickListener{ changeLanguage() }
        btn_change_language.setOnClickListener{ changeLanguage() }
    }

    private fun changeLanguage(): Unit {
        if(LocaleChecker.isRussianLocale(this)){
            setLocale("en")
        } else setLocale("ru")
        recreate()
    }

    private fun setLocale(lang: String):Unit {
        val locale: Locale = Locale(lang)
        Locale.setDefault(locale)
        val conf = Configuration()
        conf.locale = locale
        baseContext.resources.updateConfiguration(conf, baseContext.resources.displayMetrics)
        val editor: SharedPreferences.Editor = getSharedPreferences("Settings",
            AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putString("language", lang)
        editor.apply()
    }

    private fun loadLocale(){
        val prefs: SharedPreferences = getSharedPreferences("Settings",
            AppCompatActivity.MODE_PRIVATE
        )
        val language: String? = prefs.getString("language", "ru")
        language?.let { setLocale(it) }
    }


}
