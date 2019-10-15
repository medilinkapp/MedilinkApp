package com.jat.medilinkapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jat.medilinkapp.adapters.TaskAdapter

class MyDialog : DialogFragment() {
    var list = ArrayList<String>()
    val taskAdapter = TaskAdapter(list)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments != null) {
            val stringArrayList = arguments?.getStringArrayList(MainActivity.LIST)
            if (stringArrayList != null) {
                list = stringArrayList
                taskAdapter.setList(list);
            }

            if (arguments?.getBoolean(MainActivity.NOT_ALERT_DIALOG)!!) {
                return super.onCreateDialog(savedInstanceState)
            }
        }
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Tile")
        builder.setMessage("Hello! I am Alert Dialog")
        builder.setPositiveButton("Cool", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dismiss()
            }
        })
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dismiss()
            }
        })
        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.jat.medilinkapp.R.layout.fragment_dialog_task, container, false)
    }

    override fun onViewCreated(viewDialog: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewDialog, savedInstanceState)

        val btnDone = viewDialog.findViewById<Button>(com.jat.medilinkapp.R.id.btnDone)
        btnDone.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val dialogListener = activity as DialogListener
                dialogListener.onFinishEditDialog(list)
                dismiss()
            }
        })

        val btnAdd = viewDialog.findViewById<Button>(com.jat.medilinkapp.R.id.bt_add)
        btnAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val editText = viewDialog.findViewById<EditText>(com.jat.medilinkapp.R.id.et_task)
                if (!TextUtils.isEmpty(editText.text.toString())) {
                    list.add(editText.text.toString())
                    taskAdapter.notifyDataSetChanged()
                    editText.setText("")
                }
            }
        })

        val editText = viewDialog.findViewById<EditText>(com.jat.medilinkapp.R.id.et_task)

        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!TextUtils.isEmpty(editText.text.toString())) {
                    list.add(editText.text.toString())
                    taskAdapter.notifyDataSetChanged()
                    editText.setText("")
                }
                true
            } else {
                false
            }
        }

        dialog.setCanceledOnTouchOutside(false)

        with(viewDialog.findViewById(com.jat.medilinkapp.R.id.rv_tasks) as RecyclerView) {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Hey", "onCreate")
        var setFullScreen = false
        if (arguments != null) {
            setFullScreen = requireNotNull(arguments?.getBoolean("fullScreen"))
        }
        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        else
            setStyle(DialogFragment.STYLE_NORMAL, com.jat.medilinkapp.R.style.WideDialog)

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    interface DialogListener {
        fun onFinishEditDialog(inputText: ArrayList<String>)
    }
}