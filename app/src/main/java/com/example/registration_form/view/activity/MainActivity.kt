package com.example.registration_form.view.activity

import android.app.Activity
import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
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
import com.example.registration_form.database.TablesDataBase
import com.example.registration_form.model.Table
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var db: TablesDataBase
    private lateinit var adapter: TablesAdapter
    private lateinit var clipboard: ClipboardManager
    private lateinit var clip: ClipData
    private lateinit var userInfo: SharedPreferences
    private lateinit var orderBy: String
    private val tables = ArrayList<Table>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = TablesDataBase.getInstance(this)
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE)
        orderBy = userInfo.getString("sortMode", "date DESC")!!
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rv_forms.layoutManager = linearLayoutManager
        adapter = TablesAdapter(this, tables)
        rv_forms.adapter = adapter
        if (!userInfo.getBoolean("usingRoom", false))
            copyToRoomDataBase()

        try {
            upDateList()
        } catch (E: java.lang.Exception) {
            Toast.makeText(this, "載入時發生錯誤", Toast.LENGTH_SHORT).show()
        }

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
                        orderBy = if (i == 0) "date DESC" else "date ASC"
                        upDateList()
                        val editor = userInfo.edit()
                        editor.putString("sortMode", orderBy)
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
                val unPaid = it.getInt("unPaid")
                saveToDB(id, status, paid, unPaid)
            }
            upDateList()
        }
    }

    private fun upDateList() {
        val handler = Handler {
            adapter.notifyDataSetChanged()
            if (tables.size == 0) {
                tv_tip.isVisible = true
                rv_forms.isVisible = false
            } else {
                tv_tip.isVisible = false
                rv_forms.isVisible = true
            }
            true
        }
        AsyncTask.execute {
            tables.clear()
            val t = db.tableDao().getTableList(orderBy)
            tables.addAll(t)
            val msg = Message()
            msg.what = 1
            handler.sendMessage(msg)
        }
    }

    private fun deleteAll() {
        try {
            Thread {
                db.tableDao().deleteAll()
                runOnUiThread {
                    upDateList()
                }
            }.start()
            Toast.makeText(this, "已刪除所有表格", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "刪除失敗", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToDB(id: Long, status: String, paid: Int, unpaid: Int) {
        try {
            Thread {
                val table = db.tableDao().getTableByID(id)
                table.status = status
                table.paidCount = paid
                table.unpaidCount = unpaid
                db.tableDao().update(table)
                runOnUiThread {
                    upDateList()
                }
            }.start()
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
            Thread {
                db.tableDao().deleteById(id)
                runOnUiThread {
                    upDateList()
                }
            }.start()
        } catch (e: Exception) {
            Toast.makeText(this, "表格刪除失敗", Toast.LENGTH_SHORT).show()
        }
    }

    fun copy(members: ArrayList<String>, status: String) {
        var msg = ""
        for (i in 0 until members.size) {
            if (status[i] == 'f') {
                if (msg != "")
                    msg += ",\t"
                msg += members[i]
            }
        }
        clip = ClipData.newPlainText("msg", msg)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "已複製", Toast.LENGTH_SHORT).show()
    }

    private fun copyToRoomDataBase() {
        val sqlDb = TablesDB(this).writableDatabase
        val data = sqlDb.rawQuery("SELECT * FROM tables", null)
        val list = ArrayList<Table>()
        data.moveToFirst()
        for (i in 0 until data.count) {
            val id = data.getString(0).toLong()
            val title = data.getString(1)
            val date = data.getString(2)
            val members = data.getString(3)
            val status = data.getString(4)
            val paid = data.getInt(5)
            val unpaid = status.length - paid
            val organization = data.getString(6)
            val owner = data.getString(7)
            val t =
                Table(id, title, date, members, status, paid, unpaid, organization, owner)
            list.add(t)
            data.moveToNext()
        }
        data.close()
        Thread {
            db.tableDao().insertAll(list)
            runOnUiThread {
                upDateList()
            }
        }.start()
        sqlDb.execSQL("DROP TABLE IF EXISTS tables")
        userInfo.edit().putBoolean("usingRoom", true).apply()
        sqlDb.close()
    }
}
