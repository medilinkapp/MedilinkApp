package com.jat.medilinkapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.jat.medilinkapp.adapters.ClientGpsAdapter
import com.jat.medilinkapp.model.entity.ClientGps
import com.jat.medilinkapp.util.ISingleActionParameterCallBack
import com.jat.medilinkapp.util.SupportUI
import com.jat.medilinkapp.viewmodels.ClientGpsViewModal
import com.microsoft.appcenter.utils.HandlerUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_dialog_client_gps.*

class ClientGpsActivity : AppCompatActivity(), ClientGpsAdapter.DialogListener {


    private var clientGpsAdapter: ClientGpsAdapter? = null
    private var viewModel: ClientGpsViewModal? = null
    private val disposables = CompositeDisposable()
    private val supportUI: SupportUI = SupportUI()

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    companion object {
        const val KEY_CLIENT_ID = "KEY_CLIENT_ID"
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_gps)

        viewModel = ViewModelProviders.of(this).get(ClientGpsViewModal::class.java)
        initUI()

        if (intent.hasExtra(KEY_CLIENT_ID)) {
            val clientId = intent.getIntExtra(KEY_CLIENT_ID, 0)
            addClient(clientId.toString())
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initUI() {
        btBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                finish()
            }
        })

        btAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (!etClientid.text.trim().isBlank()) {
                    val value = etClientid.text.toString()
                    if (viewModel!!.getListByClientID(Integer.valueOf(value)).isEmpty()) {
                        addClient(value)
                    } else {
                        SupportUI().showDialogInfoUser(this@ClientGpsActivity, "Error", "Client already exist!", false, null);
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

        disposables.add(viewModel!!.list.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.trampoline())
                .subscribe(Consumer { list ->
                    HandlerUtils.runOnUiThread(Runnable { clientGpsAdapter!!.setList(ArrayList(list)); })
                }));

        clientGpsAdapter = ClientGpsAdapter(ArrayList(), this as ClientGpsAdapter.DialogListener)
        with(rv_client_gps as RecyclerView) {
            adapter = clientGpsAdapter
            val linearLayoutManager = LinearLayoutManager(this@ClientGpsActivity);
            linearLayoutManager.reverseLayout = true;
            layoutManager = linearLayoutManager
        }
    }


    private fun addClient(value: String) {
        etClientid.setText(value)
        SupportUI.showKeyboard(etClientid, this)
        SupportUI.showDialogRequestPassword(
                this, "Password",
                "Enter password ${if (BuildConfig.DEBUG || true) SupportUI.getPassword() else ""}",
                ISingleActionParameterCallBack { value ->
                    if (SupportUI.validatePassword(value.trim())) {
                        getLastLocation()
                    } else {
                        SupportUI().showDialogInfoUser(this, getString(R.string.prompt_password), getString(R.string.invalid_password), false, null);
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onFinishSelectionDataClientGpsDelete(clientGps: ClientGps) {
        SupportUI.showDialogRequestPassword(this, "Password",
                if (BuildConfig.DEBUG || true) "Enter password " + SupportUI.getPassword() else "Enter password"
        ) { value ->
            if (SupportUI.validatePassword(value.trim { it <= ' ' })) {
                SupportUI().showDialogInfoUser(this,
                        "Successful", "Client Deleted", true) { }
                viewModel?.delete(clientGps)
            } else {
                SupportUI().showDialogInfoUser(this, getString(R.string.prompt_password), getString(R.string.invalid_password), false, null)
            }
        }
    }

    override fun onDeleteDataClientGps() {

    }


    //----------------------------------------


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    //if (location == null) {
                        requestNewLocationData()
                   // } else {
                     //   addClientGps(location)
                   // }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            addClientGps(mLastLocation)
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }


    fun addClientGps(mLastLocation: Location) {
        val clientGps = ClientGps()
        clientGps.clientId = Integer.parseInt(etClientid.text.toString())
        clientGps.latitude = mLastLocation.latitude
        clientGps.longitude = mLastLocation.longitude
        viewModel!!.addData(clientGps)
        etClientid.text.clear()
        SupportUI().showDialogInfoUser(this, "Successful", "Client Added", true, null)
    }

}
