package com.alexeymerov.weatherfm.utils

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alexeymerov.weatherfm.R

@SuppressLint("InflateParams")
fun AppCompatActivity.createAlert(
    @StringRes titleStringId: Int,
    @StringRes dialogStringId: Int,
    @StringRes positiveButtonStringId: Int = R.string.done,
    @StringRes negativeButtonStringId: Int = R.string.cancel,
    onPositiveClick: () -> Unit = {}
) = createAlert(
    titleStringId,
    getString(dialogStringId),
    positiveButtonStringId,
    negativeButtonStringId,
    onPositiveClick
)

@SuppressLint("InflateParams")
fun AppCompatActivity.createAlert(
    @StringRes titleStringId: Int,
    dialogString: String,
    @StringRes positiveButtonStringId: Int = R.string.done,
    @StringRes negativeButtonStringId: Int = R.string.cancel,
    onPositiveClick: () -> Unit = {}
) = createAlert(getString(titleStringId), dialogString, positiveButtonStringId, negativeButtonStringId, onPositiveClick)

@SuppressLint("InflateParams")
fun AppCompatActivity.createAlert(
    titleString: String = "",
    dialogString: String,
    @StringRes positiveButtonStringId: Int = R.string.done,
    @StringRes negativeButtonStringId: Int = R.string.cancel,
    onPositiveClick: () -> Unit = {}
) {
    AlertDialog.Builder(this).apply {
        setTitle(titleString)
        setMessage(dialogString)
        setPositiveButton(getString(positiveButtonStringId)) { _, _ -> onPositiveClick.invoke() }
        if (negativeButtonStringId != 0) setNegativeButton(getString(negativeButtonStringId).toUpperCase()) { _, _ -> }

        with(create()) {
            window?.attributes?.windowAnimations = R.style.DialogAnimation
            show()
        }
    }

}