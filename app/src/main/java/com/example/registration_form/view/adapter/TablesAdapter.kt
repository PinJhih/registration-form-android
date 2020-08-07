package com.example.registration_form.view.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.registration_form.R
import com.example.registration_form.model.Table
import com.example.registration_form.view.activity.MainActivity
import kotlinx.android.synthetic.main.item_table.view.*

class TablesAdapter(
    private val context: Context,
    private val tables: ArrayList<Table>
) :
    RecyclerView.Adapter<TablesAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_table, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = tables.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = holder.itemView
        val numUnpaid = tables[position].unpaidCount
        val list = arrayOf("編輯表格", "複製未繳交成員名單", "刪除表格")
        val alertDialog = AlertDialog.Builder(context)
            .setItems(list) { _, i ->
                when (i) {
                    0 -> (context as MainActivity).edit(
                        tables[position].id,
                        tables[position].members.toArrayList(),
                        tables[position].title,
                        tables[position].status
                    )
                    1 -> (context as MainActivity).copy(
                        tables[position].members.toArrayList(),
                        tables[position].status
                    )
                    else -> AlertDialog.Builder(context)
                        .setTitle("刪除表格")
                        .setMessage("確定要刪除表格嗎?")
                        .setPositiveButton("是") { _, _ ->
                            (context as MainActivity).delete(tables[position])
                        }
                        .setNegativeButton("否") { _, _ -> }
                        .show()
                }
            }
        val textPaid = SpannableString("已繳交\n${tables[position].paidCount}")
        textPaid.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0, 3,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val textUnpaid = SpannableString("未繳交\n$numUnpaid")
        textUnpaid.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0, 3,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val date = tables[position].date

        itemView.tv_title.text = tables[position].title
        itemView.tv_paid.text = textPaid
        itemView.tv_unpaid.text = textUnpaid
        itemView.tv_date.text = date
        itemView.setOnClickListener {
            alertDialog.show()
        }
    }

    private fun String.toArrayList(): ArrayList<String> {
        val list = ArrayList<String>()
        var member = ""

        for (i in this.indices + 1) {
            if (i == this.length || this[i] == ',') {
                list.add(member)
                member = ""
            } else {
                member += this[i]
            }
        }
        return list
    }
}
