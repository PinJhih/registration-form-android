package com.example.registration_form.view.activity

import android.app.Activity
import android.content.*
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.registration_form.R
import com.example.registration_form.view.adapter.TablesAdapter
import com.example.registration_form.TablesDB
import com.example.registration_form.model.Table
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
    private lateinit var userInfo: SharedPreferences
    private lateinit var sortMode: String
    private val tables = ArrayList<Table>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = TablesDB(this).writableDatabase
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE)
        sortMode = userInfo.getString("sortMode", "DESC")!!
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort -> {
                val options = arrayOf("新到舊", "舊到新")
                AlertDialog.Builder(this)
                    .setTitle("排序方式")
                    .setItems(options) { _, i ->
                        sortMode = if (i == 0) "DESC" else "ASC"
                        upDateList()
                        val editor = userInfo.edit()
                        editor.putString("sortMode", sortMode)
                        editor.apply()
                    }
                    .show()
            }
            R.id.deleteAll -> {
                AlertDialog.Builder(this)
                    .setTitle("確認刪除?")
                    .setPositiveButton("確認") { _, _ ->
                        deleteAll()
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .show()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.extras?.let {
                val id = it.getLong("id")
                val status = it.getString("status")!!
                val paid = it.getInt("paid")
                saveToDB(id, status, paid)
            }
            upDateList()
        }
    }

    private fun upDateList() {
        tables.clear()
        adapter.notifyDataSetChanged()
        val data = db.rawQuery("SELECT * FROM tables ORDER BY date $sortMode", null)
        if (data.count == 0) {
            tables.clear()
            adapter.notifyDataSetChanged()
            tv_tip.isVisible = true
            rv_forms.isVisible = false
        } else {
            tv_tip.isVisible = false
            rv_forms.isVisible = true
            data.moveToFirst()
            for (i in 0 until data.count) {
                val id = data.getString(0).toLong()
                val title = data.getString(1)
                val date = data.getString(2)
                val members = data.getString(3)
                val status = data.getString(4)
                val paid = data.getInt(5)
                val organization = data.getString(6)
                val owner = data.getString(7)
                val form = Table(id, title, date, members, status, paid, organization, owner)
                tables.add(form)
                data.moveToNext()
                adapter.notifyDataSetChanged()
            }
        }
        data.close()
    }

    private fun deleteAll() {
        try {
            db.execSQL("DELETE FROM tables")
            upDateList()
            Toast.makeText(this, "已刪除所有表格", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "刪除失敗", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToDB(id: Long, status: String, paid: Int) {
        try {
            db.execSQL("UPDATE tables SET status = '$status' WHERE id LIKE '$id'")
            db.execSQL("UPDATE tables SET paid = '$paid' WHERE id LIKE '$id'")
        } catch (e: Exception) {
            Toast.makeText(this, "表格更新失敗", Toast.LENGTH_SHORT).show()
        }
    }

    fun edit(id: Long, members: ArrayList<String>, title: String, status: String) {
        val i = Intent(this, EditTableActivity::class.java)
        val b = Bundle()
        b.putLong("id", id)
        b.putString("title", title)
        b.putStringArrayList("members", members)
        b.putString("status", status)
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

    fun copy(members: ArrayList<String>, status: String) {
        var msg = ""
        for (i in 0 until members.size) {
            if (status[i] == 't') {
                if (msg != "")
                    msg += ",\t"
                msg += members[i]
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
