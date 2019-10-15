package com.jat.medilinkapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePreferencesUtil {
  private Context context;

  public SharePreferencesUtil(Context context) {
    this.context = context;
  }

  public void setValue(String key, String value) {
    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
    edit.putString(key, value);
    edit.apply();
  }

  public void setValue(String key, boolean value) {
    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
    edit.putBoolean(key, value);
    edit.apply();
  }

  public String getValue(String key, String defaulValue) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String value = sharedPreferences.getString(key, defaulValue);
    return value;
  }

  public boolean getValue(String key, boolean defaulValue) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    boolean value = sharedPreferences.getBoolean(key, defaulValue);
    return value;
  }
}
