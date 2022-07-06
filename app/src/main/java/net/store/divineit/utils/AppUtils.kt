package net.store.divineit.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.store.divineit.R

fun showErrorToast(context: Context, message: String) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
        val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.toast_custom_error, null)
        val textView: TextView = toastView.findViewById(R.id.errorMessage)
        textView.text = message
        toast.view = toastView
        toast.show()
    }
}

fun showWarningToast(context: Context, message: String) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
        val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.toast_custom_warning, null)
        val textView: TextView = toastView.findViewById(R.id.warningMessage)
        textView.text = message
        toast.view = toastView
        toast.show()
    }
}

fun showSuccessToast(context: Context, message: String) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
        val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.toast_custom_success, null)
        val textView: TextView = toastView.findViewById(R.id.successMessage)
        textView.text = message
        toast.view = toastView
        toast.show()
    }
}