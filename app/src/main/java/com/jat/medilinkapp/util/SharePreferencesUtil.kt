package com.jat.medilinkapp.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharePreferencesUtil(private val context: Context) {

    fun setValue(key: String, value: String) {
        val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
        edit.putString(key, value)
        edit.apply()
    }

    fun setValue(key: String, value: Boolean) {
        val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
        edit.putBoolean(key, value)
        edit.apply()
    }

    fun getValue(key: String, defaulValue: String): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, defaulValue)
    }

    fun getValue(key: String, defaulValue: Boolean): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(key, defaulValue)
    }
}
