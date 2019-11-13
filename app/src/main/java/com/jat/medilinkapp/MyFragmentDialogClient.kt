package com.jat.medilinkapp

import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jat.medilinkapp.adapters.ClientGpsAdapter
import com.jat.medilinkapp.model.entity.ClientGps
import com.jat.medilinkapp.viewmodels.ClientGpsViewModal
import com.microsoft.appcenter.utils.HandlerUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_dialog_client_gps.*
import kotlinx.android.synthetic.main.fragment_dialog_client_gps.view.*
import kotlinx.android.synthetic.main.fragment_dialog_nfc_history.view.*


class MyFragmentDialogClient : DialogFragment() {
    private var clientGpsAdapter: ClientGpsAdapter? = null
    private var viewModel: ClientGpsViewModal? = null
    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(ClientGpsViewModal::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.jat.medilinkapp.R.layout.fragment_dialog_client_gps, container, false)
    }

    override fun onViewCreated(viewDialog: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewDialog, savedInstanceState)

        btBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                dismiss()
            }
        })

        btAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                AsyncTask.execute(Runnable {
                    //viewModel!!.deleteAll()
                    dismiss()
                    (activity as MyFragmentDialogClient.DialogListener).onDeleteDataClientGps();
                })
            }
        })

        dialog.setCanceledOnTouchOutside(false)

        disposables.add(viewModel!!.list.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.trampoline())
                .subscribe(Consumer { list ->
                    HandlerUtils.runOnUiThread(Runnable { clientGpsAdapter!!.setList(ArrayList(list)); })
                }));

        clientGpsAdapter = ClientGpsAdapter(ArrayList(), activity as DialogListener)
        with(viewDialog.rv_client_gps as RecyclerView) {
            adapter = clientGpsAdapter
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
        fun onFinishSelectionDataClientGps(clientGps: ClientGps)
        fun onDeleteDataClientGps()
    }
}