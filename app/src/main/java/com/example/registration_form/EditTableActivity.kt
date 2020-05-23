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
    private var members = ArrayList<Int>()
    private var status = ArrayList<Boolean>()
    private var id = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_table)

        intent?.extras?.let {
            id = it.getLong("id")
            val m = it.getIntegerArrayList("members")!!
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

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView_edit.loadAd(adRequest)
        adView_edit.adListener = object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
            }
        }
    }

    fun editStatus(index: Int, number: Int) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.finish) {
            val intent = Intent()
            val b = Bundle()
            var s = ""
            for (i in 0 until status.size)
                s += if (status[i]) "t" else "f"
            b.putLong("id", id)
            b.putString("status", s)
            intent.putExtras(b)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }
}
