package com.example.registration_form

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_edit_form.*

class EditFormActivity : AppCompatActivity() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: MembersAdapter
    private var members = ArrayList<Int>()
    private var status = ArrayList<Boolean>()
    private var id = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_form)

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

        btn_finish.setOnClickListener {
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
    }

    fun editStatus(index: Int, number: Int) {
        AlertDialog.Builder(this)
            .setTitle("確認修改")
            .setMessage("將${number}的繳交狀態設為${!status[index]}?")
            .setPositiveButton("確認") { _, _ ->
                status[index] = !status[index]
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }
}
