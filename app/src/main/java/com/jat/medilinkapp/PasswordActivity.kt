package com.jat.medilinkapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jat.medilinkapp.util.SharePreferencesUtil
import com.jat.medilinkapp.util.SupportUI
import com.jat.medilinkapp.util.SupportUI.getPassword

class PasswordActivity : AppCompatActivity() {

    val key = "KEY_UNLOCK"

    val sharePreferencesUtil: SharePreferencesUtil
        get() {
            return SharePreferencesUtil(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        val unLock = sharePreferencesUtil.getValue(key, false)

        if (unLock) {
            startActivity(Intent(this, Welcome::class.java))
            finish()
            //sharePreferencesUtil.setValue(key, false)
        }

        findViewById<Button>(R.id.bt_unlock).setOnClickListener(View.OnClickListener {
            val edPassword = findViewById<EditText>(R.id.et_password)

            if (SupportUI.validatePassword(edPassword.text.toString())) {
                startActivity(Intent(this, Welcome::class.java))
                finish()
                sharePreferencesUtil.setValue(key, true)
            } else {
                SupportUI().showError(this, "Password", "Invalid password!", false);
                edPassword.text.clear()
            }
        })

        findViewById<TextView>(R.id.tv_test).text = getPassword();
    }


}
