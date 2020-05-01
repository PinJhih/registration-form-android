package com.example.registration_form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_form.view.*

class FormsAdapter(
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

        itemView.tv_title.text = forms[position].title
        itemView.tv_num_members.text = textMembers
            itemView.tv_num_paid.text = textPaid
        itemView.tv_num_unpaid.text = textUnPaid
    }
}