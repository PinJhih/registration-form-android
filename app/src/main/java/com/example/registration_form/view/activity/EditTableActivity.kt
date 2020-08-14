package com.example.registration_form.view.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.registration_form.view.adapter.MembersAdapter
import com.example.registration_form.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_edit_table.*

class EditTableActivity : AppCompatActivity() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: MembersAdapter
    private var members = ArrayList<String>()
    private var status = ArrayList<Char>()
    private var id = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_table)

        intent?.extras?.let {
            title = it.getString("title")
            id = it.getLong("id")
            val m = it.getStringArrayList("members")!!
            val s = it.getString("status")!!
            for (i in 0 until m.size) {
                members.add(m[i])
                status.add(s[i])
            }
        }

        gridLayoutManager = GridLayoutManager(this, 5)
        rv_members.layoutManager = gridLayoutManager
        adapter = MembersAdapter(this, members, status)
        rv_members.adapter = adapter

        btn_select_all.setOnClickListener {
            editWholeTable('t')
        }
        btn_cancel_all.setOnClickListener {
            editWholeTable('f')
        }

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView_edit.loadAd(adRequest)
        adView_edit.adListener = object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
            }
        }
    }

    fun editStatus(index: Int, number: String) {
        val msg = if (status[index] == 'f') "已繳交" else "未繳交"
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("確認修改")
            .setMessage("將${number}號的繳交狀態設為$msg?")
            .setPositiveButton("確認") { _, _ ->
                status[index] = if (status[index] == 'f') 't' else 'f'
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("取消") { _, _ -> }
        if (status[index] != 'd') {
            alertDialog.setNeutralButton("移除成員") { _, _ ->
                status[index] = 'd'
                adapter.notifyDataSetChanged()
            }
        }
        alertDialog.show()
    }

    private fun editWholeTable(value: Char) {
        val msg = if (value == 't') "\"已繳交\"" else "\"未繳交\""
        AlertDialog.Builder(this)
            .setTitle("確定修改?")
            .setMessage("確定將所有成員設為 $msg 嗎?")
            .setPositiveButton("確認") { _, _ ->
                for (i in 0 until members.count()) {
                    status[i] = value
                }
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }

    private fun saveAndFinish() {
        val intent = Intent()
        val b = Bundle()
        var s = ""
        for (i in status)
            s += i
        b.putLong("id", id)
        b.putString("status", s)
        b.putInt("paid", search('t'))
        b.putInt("unPaid", search('f'))
        intent.putExtras(b)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun search(target: Char): Int {
        var sum = 0
        for (i in 0 until status.count())
            if (status[i] == target)
                sum++
        return sum
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("儲存")
            .setMessage("確定儲存變更嗎?")
            .setPositiveButton("儲存") { _, _ ->
                saveAndFinish()
            }
            .setNegativeButton("返回") { _, _ -> }
            .setNeutralButton("放棄變更") { _, _ ->
                finish()
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.finish) {
            saveAndFinish()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }
}
