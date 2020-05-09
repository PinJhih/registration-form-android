package com.example.registration_form

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
import kotlinx.android.synthetic.main.item_form.view.*

class FormsAdapter(
    private val context: Context,
    private val forms: ArrayList<Form>
) :
    RecyclerView.Adapter<FormsAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_form, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = forms.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = holder.itemView
        var numPaid = 0
        for (i in forms[position].status)
            if (i)
                numPaid++
        val numUnpaid = forms[position].memberList.size - numPaid
        val list = arrayOf("編輯表格", "複製未繳交成員名單", "刪除表格")
        val alertDialog = AlertDialog.Builder(context)
            .setItems(list) { _, i ->
                when (i) {
                    0 -> (context as MainActivity).edit(
                        forms[position].id,
                        forms[position].memberList,
                        forms[position].status
                    )
                    1 -> (context as MainActivity).copy(
                        forms[position].memberList,
                        forms[position].status
                    )
                    else -> (context as MainActivity).delete(forms[position].id)
                }
            }
        val textPaid = SpannableString("已繳交\n$numPaid")
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
        itemView.tv_title.text = forms[position].title
        itemView.tv_paid.text = textPaid
        itemView.tv_unpaid.text = textUnpaid
        itemView.setOnClickListener {
            alertDialog.show()
        }
    }
}
