package com.example.userblinkit_clone

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

object Utils {

    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, msg: String) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_dialog_box, null)

        val textView = view.findViewById<TextView>(R.id.tv_ctm_dialog)
        textView.text = msg

        builder.setView(view)
        builder.setCancelable(false) // Make dialog non-cancelable if required

        dialog = builder.create()
        dialog?.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private var authInstance: FirebaseAuth? = null
    fun getAuth(): FirebaseAuth {
        if (authInstance == null) {
            authInstance = FirebaseAuth.getInstance()
        }
        return authInstance!!
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().getCurrentUser()?.getUid() ?: ""
    }
}
