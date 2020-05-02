package com.example.registration_form

import android.content.Context
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
        val numMembers = forms[position].memberList.count()
        var numPaid = 0
        for (i in forms[position].status)
            if (i)
                numPaid++
        val numUnPaid = numMembers - numPaid
        val textMembers = "總人數:$numMembers"
        val textPaid = "已繳交:$numPaid"
        val textUnPaid = "未繳交:$numUnPaid"
        val list = arrayOf("編輯表格", "複製未繳交成員名單", "刪除表格")
        val alertDialog = AlertDialog.Builder(context)
            .setItems(list) { _, i ->
                when (i) {
                    0 -> (context as MainActivity).edit(forms[position].id)
                    1 -> (context as MainActivity).copy(
                        forms[position].memberList,
                        forms[position].status
                    )
                    else -> (context as MainActivity).delete(forms[position].id)
                }
            }
        itemView.tv_title.text = forms[position].title
        itemView.tv_num_members.text = textMembers
        itemView.tv_num_paid.text = textPaid
        itemView.tv_num_unpaid.text = textUnPaid

        itemView.setOnClickListener {
            alertDialog.show()
        }
    }
}
