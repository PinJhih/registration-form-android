package com.example.registration_form

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    private lateinit var adapter: FormsAdapter
    private val forms = ArrayList<Form>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FormsDB(this).writableDatabase

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rv_forms.layoutManager = linearLayoutManager
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rv_forms.addItemDecoration(decoration)
        adapter = FormsAdapter(this, forms)
        rv_forms.adapter = adapter
        upDateList()

        btn_start_edit.setOnClickListener {
            val i = Intent(this, AddFormActivity::class.java)
            startActivityForResult(i, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        upDateList()
    }

    private fun upDateList() {
        forms.clear()
        val data = db.rawQuery("SELECT * FROM forms", null)
        data.moveToFirst()

        for (i in 0 until data.count) {
            val id = data.getString(0).toLong()
            val title = data.getString(1)
            val members = toMemberList(data.getString(2))
            val status = toStatusList(data.getString(3))
            val form = Form(id, title, members, status)
            forms.add(form)
            adapter.notifyDataSetChanged()
            data.moveToNext()
        }
        data.close()
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

        for (i in members.indices) {
            if (i + 1 == members.length || members[i] == ',') {
                list.add(member)
                member = 0
            } else {
                member *= 10
                member += members[i].toInt() - 48
            }
        }
        return list
    }

}
