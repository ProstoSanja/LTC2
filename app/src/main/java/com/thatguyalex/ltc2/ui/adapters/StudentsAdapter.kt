package com.thatguyalex.ltc2.ui.adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thatguyalex.ltc2.R
import com.thatguyalex.ltc2.api.classes.StudentResponse


class StudentsAdapter(private var dataSet: List<StudentResponse>,
                      private val context: Context,
                      private val setAttendanceCallback: (barcode: String, reportError: Boolean) -> Unit,
                      private val deleteAttendanceCallback: (barcode: String) -> Unit
) : RecyclerView.Adapter<StudentsAdapter.ViewHolder>() {

    fun updateDataSet(dataSet: List<StudentResponse>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view
        val name: AppCompatTextView = view.findViewById(R.id.student_name)
        val checkbox: MaterialCheckBox = view.findViewById(R.id.student_checkbox)
        val imageMissingLongTime: ImageView = view.findViewById(R.id.student_missing_long_time)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_student_entry, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val student = dataSet[position]
        viewHolder.name.text = student.name
        viewHolder.checkbox.isChecked = student.attendedToday
        viewHolder.imageMissingLongTime.visibility = if (student.attendedPastTwoWeeks) View.GONE else View.VISIBLE
        viewHolder.checkbox.setOnClickListener { view ->
            val checkbox = (view as AppCompatCheckBox)
            val checked = checkbox.isChecked
            showDialog(checked, { _,_ ->
                if (checked) {
                    setAttendanceCallback(student.barcode, true)
                } else {
                    deleteAttendanceCallback(student.barcode)
                }
            }, {_,_ ->
                checkbox.isChecked = !checkbox.isChecked
            })
        }
    }


    fun showDialog(checked: Boolean, action: DialogInterface.OnClickListener, cancel: DialogInterface.OnClickListener) {
        MaterialAlertDialogBuilder(context)
            .setTitle(if (checked) "Register attendance" else "Delete attendance")
            .setMessage("Confirm your action?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("yes", action)
            .setNegativeButton("no", cancel)
            .setCancelable(false)
            .show()

    }

    override fun getItemCount() = dataSet.size

}

