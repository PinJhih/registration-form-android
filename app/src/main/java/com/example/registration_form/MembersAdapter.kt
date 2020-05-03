package com.example.registration_form

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_member.view.*

class MembersAdapter(
    private val members: ArrayList<Int>,
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

        itemView.tv_member.text = "${members[position]}"
        itemView.tv_member.setTextColor(
            if (status[position]) Color.parseColor("#3C783C")
            else Color.parseColor("#AA4343")
        )
        itemView.tv_member.setBackgroundColor(
            if (status[position]) Color.parseColor("#D9CBFFC4")
            else Color.parseColor("#D9FFC0C0")
        )
    }
}

