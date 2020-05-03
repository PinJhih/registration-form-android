package com.example.registration_form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_edit_form.*

class EditFormActivity : AppCompatActivity() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: MembersAdapter
    private var members = ArrayList<Int>()
    private var status = ArrayList<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_form)

        intent?.extras?.let {
            val m = it.getIntegerArrayList("members")!!
            val s = it.getBooleanArray("status")!!

            for (i in 0 until m.size) {
                members.add(m[i])
                status.add(s[i])
            }
        }

        gridLayoutManager = GridLayoutManager(this, 5)
        rv_members.layoutManager = gridLayoutManager
        adapter = MembersAdapter(members, status)
        rv_members.adapter = adapter
    }
}
