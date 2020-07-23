package com.example.registration_form

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_edit_table.*

class EditTableActivity : AppCompatActivity() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: MembersAdapter
    private var members = ArrayList<String>()
    private var status = ArrayList<Boolean>()
    private var id = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_table)

        intent?.extras?.let {
            id = it.getLong("id")
            val m = it.getStringArrayList("members")!!
            val s = it.getBooleanArray("status")!!

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
            editWholeTable(true)
        }
        btn_cancel_all.setOnClickListener {
            editWholeTable(false)
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
        val msg = if (!status[index]) "已繳交" else "未繳交"
        AlertDialog.Builder(this)
            .setTitle("確認修改")
            .setMessage("將${number}號的繳交狀態設為$msg?")
            .setPositiveButton("確認") { _, _ ->
                status[index] = !status[index]
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }

    private fun editWholeTable(goal: Boolean) {
        val msg = if (goal) "\"已繳交\"" else "\"未繳交\""
        AlertDialog.Builder(this)
            .setTitle("確定修改?")
            .setMessage("確定將所有成員設為 $msg 嗎?")
            .setPositiveButton("確認") { _, _ ->
                for (i in 0 until members.count()) {
                    status[i] = goal
                }
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }

    private fun saveAndFinish() {
        AlertDialog.Builder(this)
            .setTitle("儲存")
            .setMessage("確定儲存變更嗎?")
            .setPositiveButton("儲存") { _, _ ->
                val intent = Intent()
                val b = Bundle()
                var s = ""
                for (i in 0 until status.size)
                    s += if (status[i]) "t" else "f"
                b.putLong("id", id)
                b.putString("status", s)
                b.putInt("paid", getPaidCount())
                intent.putExtras(b)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            .setNegativeButton("返回") { _, _ -> }
            .setNeutralButton("放棄變更") { _, _ ->
                finish()
            }
            .show()
    }

    private fun getPaidCount(): Int {
        var paid = 0
        for (i in 0 until status.count())
            if (status[i])
                paid++
        return paid
    }

    override fun onBackPressed() {
        saveAndFinish()
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
