package com.jat.medilinkapp

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jat.medilinkapp.adapters.ClientGpsAdapter
import com.jat.medilinkapp.model.entity.ClientGps
import com.jat.medilinkapp.util.ISingleActionParameterCallBack
import com.jat.medilinkapp.util.SupportUI
import com.jat.medilinkapp.viewmodels.ClientGpsViewModal
import com.microsoft.appcenter.utils.HandlerUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_dialog_client_gps.*
import kotlinx.android.synthetic.main.fragment_dialog_client_gps.view.*


class MyFragmentDialogClient : DialogFragment() {
    private var clientGpsAdapter: ClientGpsAdapter? = null
    private var viewModel: ClientGpsViewModal? = null
    private val disposables = CompositeDisposable()
    private val supportUI: SupportUI = SupportUI()

    companion object {
        const val KEY_CLIENT_ID = "KEY_CLIENT_ID"
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

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

    override fun onResume() {
        super.onResume()
        arguments?.let {
            it.getInt(KEY_CLIENT_ID).let {
                val value = arguments?.getInt(KEY_CLIENT_ID).toString()
                addClient(value)
            }
        }
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
                if (!etClientid.text.trim().isBlank()) {
                    val value = etClientid.text.toString()
                    if (viewModel!!.getListByClientID(Integer.valueOf(value)).isEmpty()) {
                        addClient(value)
                    } else {
                        SupportUI().showDialogInfoUser(this@MyFragmentDialogClient.context, "Error", "Client already exist!", false, null);
                    }
                    etClientid.setError(null)
                } else
                    etClientid.setError("Empty value")
//                AsyncTask.execute(Runnable {
//                    //viewModel!!.deleteAll()
//                    (activity as MyFragmentDialogClient.DialogListener).onDeleteDataClientGps();
//                })
            }
        })

        dialog.setCanceledOnTouchOutside(false)

        disposables.add(viewModel!!.list.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.trampoline())
                .subscribe(Consumer { list ->
                    HandlerUtils.runOnUiThread(Runnable { clientGpsAdapter!!.setList(ArrayList(list)); })
                }));

        //clientGpsAdapter = ClientGpsAdapter(ArrayList(), activity as MyFragmentDialogClient.DialogListener)
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

    private fun addClient(value: String) {
        etClientid.setText(value)
        SupportUI.showKeyboard(etClientid, this@MyFragmentDialogClient.context)
        SupportUI.showDialogRequestPassword(
                this@MyFragmentDialogClient.context, "Password",
                "Enter password ${if (BuildConfig.DEBUG) SupportUI.getPassword() else ""}",
                ISingleActionParameterCallBack { value ->
                    if (SupportUI.validatePassword(value.trim())) {
                        val clientGps = ClientGps()
                        clientGps.clientId = Integer.parseInt(etClientid.text.toString())
                        //clientGps.latitude = location.latitude
                        //clientGps.longitude = location.longitude
                        viewModel!!.addData(clientGps)
                        etClientid.text.clear()
                        SupportUI().showDialogInfoUser(this@MyFragmentDialogClient.context, "Successful", "Client Added", true, null)
                    } else {
                        SupportUI().showDialogInfoUser(this@MyFragmentDialogClient.context, getString(R.string.prompt_password), getString(R.string.invalid_password), false, null);
                    }
                })
    }


    interface DialogListener {
        fun onFinishSelectionDataClientGps(clientGps: ClientGps)
        fun onDeleteDataClientGps()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }


}