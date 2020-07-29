package com.example.registration_form.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.registration_form.R
import com.example.registration_form.view.activity.EditTableActivity
import kotlinx.android.synthetic.main.item_member.view.*

class MembersAdapter(
    private val context: Context,
    private val members: ArrayList<String>,
    private val status: ArrayList<Boolean>
) :
    RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_member, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = members.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = holder.itemView

        itemView.btn_member.text = members[position]
        itemView.btn_member.setTextColor(
            if (status[position]) Color.parseColor("#3C783C")
            else Color.parseColor("#AA4343")
        )
        itemView.btn_member.setBackgroundResource(
            if (status[position]) R.drawable.bg_round_button_green
            else R.drawable.bg_round_button_red
        )
        itemView.btn_member.setOnClickListener {
            (context as EditTableActivity).editStatus(position, members[position])
        }
    }
}

