package com.thatguyalex.ltc2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thatguyalex.ltc2.R
import com.thatguyalex.ltc2.viewmodel.GroupsState

class GroupsAdapter(private var dataSet: List<GroupsState>,
                    private val callback: (groupState: GroupsState) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    fun updateDataSet(dataSet: List<GroupsState>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View
        val textView: TextView

        init {
            textView = view.findViewById(R.id.group_entry_text)
            root = view
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_group_entry, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val group = dataSet[position]
        viewHolder.textView.text = group.courseName + " - " + group.groupName
        if (!group.isTitle) {
            viewHolder.root.setOnClickListener {
                callback(group)
            }
        }
    }

    override fun getItemCount() = dataSet.size

}

