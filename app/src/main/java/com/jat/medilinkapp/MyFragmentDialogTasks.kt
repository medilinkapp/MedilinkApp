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
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jat.medilinkapp.adapters.TaskAdapter
import com.jat.medilinkapp.util.SupportUI
import kotlinx.android.synthetic.main.fragment_dialog_task.*


class MyFragmentDialogTasks : DialogFragment() {
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

        btnDone.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val dialogListener = activity as DialogListener
                dialogListener.onFinishEditDialog(list)
                dismiss()
            }
        })

        btnAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (!TextUtils.isEmpty(etTask.text.toString())) {
                    list.add(etTask.text.toString())
                    taskAdapter.notifyDataSetChanged()
                    etTask.setText("")
                }
            }
        })


        etTask.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!TextUtils.isEmpty(etTask.text.toString())) {
                    list.add(etTask.text.toString())
                    taskAdapter.notifyDataSetChanged()
                    etTask.setText("")
                }
                true
            } else {
                false
            }
        }

        SupportUI.showKeyboard(etTask, context)

        dialog.setCanceledOnTouchOutside(false)

        with(viewDialog.findViewById(com.jat.medilinkapp.R.id.rv_nfc_history) as RecyclerView) {
            adapter = taskAdapter
            layoutManager = GridLayoutManager(activity, 3)
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