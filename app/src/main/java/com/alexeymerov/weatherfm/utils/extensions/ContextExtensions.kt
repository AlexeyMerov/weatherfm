package com.alexeymerov.weatherfm.utils.extensions

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

fun Activity.hideKeyboardEx() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.apply { imm.hideSoftInputFromWindow(windowToken, 0) }
}

fun Context.showToast(text: Any) = Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()

inline fun Activity.showSnack(message: String, isLong: Boolean = false, f: Snackbar.() -> Unit = {}) {
    (this.findViewById<View>(android.R.id.content))?.showSnack(message, isLong, f)
}

fun Context.getColorEx(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun Activity.copyToClipBoard(text: String, label: String = "simpleLabel") {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.primaryClip = ClipData.newPlainText(label, text)
}

inline fun Context.isNetworkConnected(f: (Boolean) -> Unit) {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetInfo = connectivityManager.activeNetworkInfo
    f.invoke(activeNetInfo != null && activeNetInfo.isConnected)
}