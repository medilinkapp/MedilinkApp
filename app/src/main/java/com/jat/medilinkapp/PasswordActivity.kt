package com.jat.medilinkapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.jat.medilinkapp.util.SharePreferencesUtil
import com.jat.medilinkapp.util.SupportUI

class PasswordActivity : AppCompatActivity() {


    val sharePreferencesUtil: SharePreferencesUtil
        get() {
            return SharePreferencesUtil(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        val unLock = sharePreferencesUtil.getValue("KEY_UNLOCK", false)

        if (unLock) {
            startActivity(Intent(this, Welcome::class.java))
        }

        findViewById<Button>(R.id.bt_unlock).setOnClickListener(View.OnClickListener {
            val edPassword = findViewById<EditText>(R.id.et_password)

            if (SupportUI.validatePassword(edPassword.text.toString())) {

                startActivity(Intent(this, Welcome::class.java))
            }else{
                SupportUI().showError(this, "Password", "Invalid password!", false);
            }
        })
    }


}
