package com.registration_form.view.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.registration_form.R
import com.registration_form.view.activities.EditTableActivity
import kotlinx.android.synthetic.main.item_member.view.*

class MembersAdapter(
    private val context: Context,
    private val members: ArrayList<String>,
    private val status: ArrayList<Char>
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
            when (status[position]) {
                'd' -> Color.parseColor("#D91B1B1B")
                't' -> Color.parseColor("#3C783C")
                else -> Color.parseColor("#AA4343")
            }
        )
        /*
        itemView.btn_member.backgroundT(
            when (status[position]) {
                'd' -> Color.parseColor("#D9CECECE")
                't' -> Color.parseColor("#D9CBFFC4")
                else -> Color.parseColor("#D9FFC0C0")
            }
        )
        */
        itemView.btn_member.setOnClickListener {
            (context as EditTableActivity).editStatus(position, members[position])
        }
    }
}
