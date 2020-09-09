package com.example.registration_form.view.activity

import android.app.Activity
import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var clipboard: ClipboardManager
    private lateinit var clip: ClipData
    private lateinit var userInfo: SharedPreferences
    private lateinit var orderBy: String
    private lateinit var viewModel: TablesViewModel
    private var tables: List<Table> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE)
        orderBy = userInfo.getString("sortMode", "DESC")!!
        viewModel = ViewModelProvider(this).get(TablesViewModel::class.java)
        viewModel.tables.observe(this, Observer { tableList ->
            tableList?.let {
                tables = tableList
                adapter.notifyDataSetChanged()
            }
        })
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rv_forms.layoutManager = linearLayoutManager
        adapter = TablesAdapter(this, tables)
        rv_forms.adapter = adapter

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
                        orderBy = if (i == 0) "DESC" else "ASC"
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
                val position = it.getInt("position")
                val status = it.getString("status")!!
                val paid = it.getInt("paid")
                val unPaid = it.getInt("unPaid")
                saveToDB(position, status, paid, unPaid)
            }
        }
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
