package com.jat.medilinkapp

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jat.medilinkapp.adapters.NfcHistoryAdapter
import com.jat.medilinkapp.model.entity.NfcData


class MyDialogHistory : DialogFragment() {
    var list = ArrayList<NfcData>()
    val adapter = NfcHistoryAdapter(list, activity as DialogListener)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments != null) {
            val nfclist = arguments!!.getParcelableArrayList<NfcData>(MainActivity.LIST)
            if (nfclist != null) {
                list = nfclist
                adapter.setList(list);
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.jat.medilinkapp.R.layout.fragment_dialog_nfc_history, container, false)
    }

    override fun onViewCreated(viewDialog: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewDialog, savedInstanceState)

        val btBack = viewDialog.findViewById<Button>(com.jat.medilinkapp.R.id.bt_back)
        btBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val dialogListener = activity as DialogListener
                dismiss()
            }
        })

        dialog.setCanceledOnTouchOutside(false)
        with(viewDialog.findViewById(com.jat.medilinkapp.R.id.rv_tasks) as RecyclerView) {
            adapter = adapter
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
        fun onFinishSelectionData(nfcData: NfcData)
    }
}