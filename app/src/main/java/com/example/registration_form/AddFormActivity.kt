package com.example.registration_form

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_form.*

class AddFormActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_form)

        db = FormsDB(this).writableDatabase

        btn_add_form.setOnClickListener {
            if (ed_max_num.text.isEmpty() || ed_min_num.text.isEmpty() || ed_title.text.isEmpty())
                Toast.makeText(this, "請填滿所有欄位", Toast.LENGTH_SHORT).show()
            else if (ed_max_num.text.toString().toInt() < ed_min_num.text.toString().toInt())
                Toast.makeText(this, "第一位成員的編號須為最小", Toast.LENGTH_SHORT).show()
            else if (ed_max_num.text.toString().toInt() >= 1000)
                Toast.makeText(this, "超過最大值", Toast.LENGTH_SHORT).show()
            else {
                addForm()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        btn_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun addForm() {
        val id = "${System.currentTimeMillis()}"
        val title = "${ed_title.text}"
        val startNumber = ed_min_num.text.toString().toInt()
        val endNumber = ed_max_num.text.toString().toInt()
        var members = ""
        var status = ""

        try {
            for (i in startNumber until endNumber + 1) {
                members += "$i"
                status += "f"
                if (i != endNumber)
                    members += ","
            }
            db.execSQL(
                "INSERT INTO forms(id,title,members,status) VALUES(?,?,?,?)",
                arrayOf<Any?>(id, title, members, status)
            )
        } catch (e: Exception) {
            Toast.makeText(this, "表格建立失敗", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}
