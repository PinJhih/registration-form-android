package com.example.registration_form

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    private lateinit var adapter: TablesAdapter
    private lateinit var clipboard: ClipboardManager
    private lateinit var clip: ClipData
    private val tables = ArrayList<Table>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = TablesDB(this).writableDatabase
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rv_forms.layoutManager = linearLayoutManager
        adapter = TablesAdapter(this, tables)
        rv_forms.adapter = adapter
        Thread(Runnable {
            try {
                upDateList()
            } catch (ex: Exception) {
                Toast.makeText(this, "載入時發生錯誤", Toast.LENGTH_SHORT).show()
            }
        }).start()

        btn_start_edit.setOnClickListener {
            val i = Intent(this, AddTableActivity::class.java)
            startActivityForResult(i, 0)
        }

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView_main.loadAd(adRequest)
        adView_main.adListener = object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let {
            val id = it.getLong("id")
            val status = it.getString("status")!!
            saveToDB(id, status)
        }
        upDateList()
    }

    private fun upDateList() {
        tables.clear()
        adapter.notifyDataSetChanged()
        val data = db.rawQuery("SELECT * FROM tables", null)
        data.moveToFirst()

        for (i in 0 until data.count) {
            val id = data.getString(0).toLong()
            val title = data.getString(1)
            val date = data.getString(2)
            val members = toMemberList(data.getString(3))
            val status = toStatusList(data.getString(4))
            val organization = data.getString(5)
            val owner = data.getString(6)
            val form = Table(id, title, date, members, status,organization, owner)
            tables.add(form)
            data.moveToNext()
        }
        adapter.notifyDataSetChanged()
        data.close()
    }

    private fun saveToDB(id: Long, status: String) {
        try {
            db.execSQL("UPDATE tables SET status = '$status' WHERE id LIKE '$id'")
        } catch (e: Exception) {
            Toast.makeText(this, "表格更新失敗", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toStatusList(status: String): ArrayList<Boolean> {
        val list = ArrayList<Boolean>()
        for (i in status) {
            if (i == 't')
                list.add(true)
            else
                list.add(false)
        }
        return list
    }

    private fun toMemberList(members: String): ArrayList<Int> {
        val list = ArrayList<Int>()
        var member = 0

        for (i in members.indices + 1) {
            if (i == members.length || members[i] == ',') {
                list.add(member)
                member = 0
            } else {
                member *= 10
                member += members[i].toInt() - 48
            }
        }
        return list
    }

    fun edit(id: Long, members: ArrayList<Int>, status: ArrayList<Boolean>) {
        val i = Intent(this, EditTableActivity::class.java)
        val b = Bundle()
        b.putLong("id", id)
        b.putIntegerArrayList("members", members)
        b.putBooleanArray("status", status.toBooleanArray())
        i.putExtras(b)
        startActivityForResult(i, 1)
    }

    fun delete(id: Long) {
        try {
            db.execSQL("DELETE FROM tables WHERE id LIKE $id")
            upDateList()
        } catch (e: Exception) {
            Toast.makeText(this, "表格刪除失敗", Toast.LENGTH_SHORT).show()
        }
    }

    fun copy(members: ArrayList<Int>, status: ArrayList<Boolean>) {
        var msg = ""
        for (i in 0 until members.size) {
            if (!status[i]) {
                msg += "${members[i]}"
                if (i + 1 != members.size)
                    msg += ",\t"
            }
        }
        clip = ClipData.newPlainText("msg", msg)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "已複製", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}
