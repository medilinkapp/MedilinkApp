package com.jat.medilinkapp

import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jat.medilinkapp.adapters.NfcHistoryAdapter
import com.jat.medilinkapp.model.entity.NfcData
import com.jat.medilinkapp.viewmodels.NfcDataHistoryViewModel
import com.microsoft.appcenter.utils.HandlerUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer


class MyFragmentDialogHistory : DialogFragment() {
    private var nfcHistoryAdapter: NfcHistoryAdapter? = null
    private var viewModel: NfcDataHistoryViewModel? = null
    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(NfcDataHistoryViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.jat.medilinkapp.R.layout.fragment_dialog_nfc_history, container, false)
    }

    override fun onViewCreated(viewDialog: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewDialog, savedInstanceState)

        val btBack = viewDialog.findViewById<ImageView>(com.jat.medilinkapp.R.id.bt_back)
        btBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                dismiss()
            }
        })

        val btDeleteAll = viewDialog.findViewById<ImageView>(com.jat.medilinkapp.R.id.bt_delete_all)
        if (BuildConfig.DEBUG) {
            btDeleteAll.visibility = View.VISIBLE
        }
        btDeleteAll.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                AsyncTask.execute(Runnable {
                    viewModel!!.deleteAll()
                    dismiss()
                    (activity as MyFragmentDialogHistory.DialogListener).onDeleteData();

                })
            }
        })

        dialog.setCanceledOnTouchOutside(false)

        disposables.add(viewModel!!.list.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.trampoline())
                .subscribe(Consumer { list ->
                    HandlerUtils.runOnUiThread(Runnable { nfcHistoryAdapter!!.setList(ArrayList(list)); })

                    //nfcHistoryAdapter!!.notifyDataSetChanged();

                }));

        nfcHistoryAdapter = NfcHistoryAdapter(ArrayList(), activity as MyFragmentDialogHistory.DialogListener)
        with(viewDialog.findViewById(com.jat.medilinkapp.R.id.rv_tasks) as RecyclerView) {
            adapter = nfcHistoryAdapter
            val linearLayoutManager = LinearLayoutManager(activity);
            linearLayoutManager.reverseLayout = true;
            layoutManager = linearLayoutManager
        }

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
        disposables.clear()
    }

    interface DialogListener {
        fun onFinishSelectionData(nfcData: NfcData)
        fun onDeleteData()
    }
}