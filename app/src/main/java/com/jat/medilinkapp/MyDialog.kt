package com.jat.medilinkapp

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment


class MyDialog : DialogFragment() {
    val list = ArrayList<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments != null) {
            if (arguments?.getBoolean("notAlertDialog")!!) {
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
        return inflater.inflate(R.layout.fragment_my_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDone = view.findViewById<Button>(R.id.btnDone)
        btnDone.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val dialogListener = activity as DialogListener
                dialogListener.onFinishEditDialog(list)
                dismiss()
            }
        })
        dialog.setCanceledOnTouchOutside(false)


//        with(view.findViewById(R.id.rc) as RecyclerView) {
//            val taskAdapter = TaskAdapter(list)
//            adapter = taskAdapter
//            layoutManager = LinearLayoutManager(activity)
//        }

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
            setStyle(DialogFragment.STYLE_NORMAL, R.style.WideDialog)

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    interface DialogListener {
        fun onFinishEditDialog(inputText: ArrayList<String>)
    }
}