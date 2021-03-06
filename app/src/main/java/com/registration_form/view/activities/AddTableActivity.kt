package com.registration_form.view.activities

import android.app.Activity
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.registration_form.R
import com.registration_form.database.TablesDataBase
import com.registration_form.model.Table
import kotlinx.android.synthetic.main.activity_add_table.*
import java.util.*

class AddTableActivity : AppCompatActivity() {
    private lateinit var db: TablesDataBase
    private lateinit var userInfo: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_table)

        db = TablesDataBase.getInstance(this)
        userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE)
        val edit = userInfo.edit()

        if (userInfo.getBoolean("firstTimeAdd", true)) {
            edit.putBoolean("firstTimeAdd", false)
            edit.apply()
        } else {
            ed_min_num.setText("${userInfo.getInt("numMin", 0)}")
            ed_max_num.setText("${userInfo.getInt("numMax", 0)}")
        }

        btn_add_table.setOnClickListener {
            if (ed_max_num.text.isEmpty() || ed_min_num.text.isEmpty() || ed_title.text.isEmpty())
                Toast.makeText(this, "請填滿所有欄位", Toast.LENGTH_SHORT).show()
            else if (ed_max_num.text.toString().toInt() < ed_min_num.text.toString().toInt())
                Toast.makeText(this, "第一位成員的編號須為最小", Toast.LENGTH_SHORT).show()
            else if (ed_max_num.text.toString().toInt() >= 1000)
                Toast.makeText(this, "超過最大值", Toast.LENGTH_SHORT).show()
            else {
                edit.putInt("numMin", "${ed_min_num.text}".toInt())
                edit.putInt("numMax", "${ed_max_num.text}".toInt())
                edit.apply()
                addTable()
            }
        }
        btn_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        title = "新增"

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView_add.loadAd(adRequest)
        adView_add.adListener = object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
            }
        }
    }

    private fun addTable() {
        val id = System.currentTimeMillis()
        val title = "${ed_title.text}"
        val cal = Calendar.getInstance()
        cal.get(Calendar.YEAR)
        cal.get(Calendar.MONTH)
        cal.get(Calendar.DAY_OF_MONTH)
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.TAIWAN)
        val date = sdf.format(cal.time)
        val startNumber = ed_min_num.text.toString().toInt()
        val endNumber = ed_max_num.text.toString().toInt()
        var members = ""
        var status = ""
        val paid = 0
        val organization = "123"
        val owner = "123"
        for (i in startNumber until endNumber + 1) {
            members += "$i"
            status += "f"
            if (i != endNumber)
                members += ","
        }
        val unpaid = status.length
        val table =
            Table(id, title, date, members, status, paid, unpaid, organization, owner)
        try {
            Thread {
                db.tableDao().insert(table)
                runOnUiThread {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }.start()
        } catch (e: Exception) {
            Toast.makeText(this, "表格建立失敗", Toast.LENGTH_SHORT).show()
        }
    }
}