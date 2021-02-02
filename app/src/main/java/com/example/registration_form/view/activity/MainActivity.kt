package com.example.registration_form.view.activity

import android.app.Activity
import android.content.*
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.registration_form.R
import com.example.registration_form.view.adapter.TablesAdapter
import com.example.registration_form.model.Table
import com.example.registration_form.viewmodel.TablesViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TablesAdapter
    private lateinit var userInfo: SharedPreferences
    private lateinit var order: String
    private lateinit var viewModel: TablesViewModel
    private var tables: MutableList<Table> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "我的表格"
        userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE)
        if (!userInfo.getBoolean("usingRoomDB", false)) {
            try {
                this.deleteDatabase("Tables.db")
                val editor = userInfo.edit()
                editor.putBoolean("usingRoomDB", true)
                editor.apply()
            } catch (e: Exception) {
                val editor = userInfo.edit()
                editor.putBoolean("usingRoomDB", true)
                editor.apply()
                Log.i("db", "已刪除SQLiteDB")
            }
        }
        order = userInfo.getString("sortMode", "DESC")!!
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rv_tables.layoutManager = linearLayoutManager
        adapter = TablesAdapter(this, tables)
        rv_tables.adapter = adapter
        viewModel = ViewModelProvider(this).get(TablesViewModel::class.java)
        viewModel.tables.observe(this, Observer { tableList ->
            tableList?.let {
                updateList(tableList)
            }
        })

        btn_start_edit.setOnClickListener {
            val i = Intent(this, AddTableActivity::class.java)
            startActivityForResult(i, 0)
        }
    }

    override fun onStart() {
        super.onStart()
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
            R.id.search -> {
            }
            R.id.sort -> {
                val options = arrayOf("新到舊", "舊到新")
                AlertDialog.Builder(this)
                    .setTitle("排序方式")
                    .setItems(options) { _, i ->
                        order = if (i == 0) "DESC" else "ASC"
                        val editor = userInfo.edit()
                        editor.putString("sortMode", order)
                        editor.apply()
                        updateList(null)
                        adapter.notifyDataSetChanged()
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
                val position = it.getInt("position")
                val status = it.getString("status")!!
                val paid = it.getInt("paid")
                val unPaid = it.getInt("unPaid")
                saveToDB(position, status, paid, unPaid)
            }
        } else
            updateList(null)
    }

    private fun updateList(list: List<Table>?) {
        //傳入null則只重新排序
        list?.let {
            tables.clear()
            tables.addAll(list)
            if (tables.isEmpty()) {
                rv_tables.isVisible = false
                tv_tip.isVisible = true
            } else {
                rv_tables.isVisible = true
                tv_tip.isVisible = false
            }
        }
        if (order == "DESC")
            tables.sortByDescending { it.date }
        else
            tables.sortBy { it.date }
        adapter.notifyDataSetChanged()
    }

    private fun deleteAll() {
        try {
            Thread {
                viewModel.deleteAll()
            }.start()
            Toast.makeText(this, "已刪除所有表格", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "刪除失敗", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToDB(position: Int, status: String, paid: Int, unpaid: Int) {
        try {
            Thread {
                val table = tables[position]
                table.status = status
                table.paidCount = paid
                table.unpaidCount = unpaid
                viewModel.insert(table)
            }.start()
        } catch (e: Exception) {
            Toast.makeText(this, "表格更新失敗", Toast.LENGTH_SHORT).show()
        }
    }

    fun edit(position: Int, members: ArrayList<String>, title: String, status: String) {
        val i = Intent(this, EditTableActivity::class.java)
        val b = Bundle()
        b.putInt("position", position)
        b.putString("title", title)
        b.putStringArrayList("members", members)
        b.putString("status", status)
        i.putExtras(b)
        startActivityForResult(i, 1)
    }

    fun delete(target: Table) {
        try {
            Thread {
                viewModel.delete(target)
                runOnUiThread {
                    Snackbar.make(layout_main_page, "刪除成功", Snackbar.LENGTH_LONG)
                        .setAction("還原") {
                            undo(target)
                        }.show()
                }
            }.start()
        } catch (e: Exception) {
            Toast.makeText(this, "表格刪除失敗", Toast.LENGTH_SHORT).show()
        }
    }

    private fun undo(table: Table) {
        Thread {
            viewModel.insert(table)
        }.start()
    }

    fun copy(members: ArrayList<String>, status: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData
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
}
