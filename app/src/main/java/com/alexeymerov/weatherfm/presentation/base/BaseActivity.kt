package com.alexeymerov.weatherfm.presentation.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alexeymerov.weatherfm.R
import com.alexeymerov.weatherfm.utils.extensions.hideKeyboardEx
import com.alexeymerov.weatherfm.utils.extensions.isNetworkConnected
import com.alexeymerov.weatherfm.utils.extensions.makeVisible


abstract class BaseActivity : AppCompatActivity() {

    private var noInternetView: View? = null
    private var networkStateBroadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkStateBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                isNetworkConnected {
                    onInternetStateChanged(it)
                    if (it) noInternetView?.makeVisible(false)
                    else noInternetView?.makeVisible()
                }
            }
        }
    }

    open fun onInternetStateChanged(isConnected: Boolean) {

    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
            addAction("android.net.wifi.WIFI_STATE_CHANGED")
        }
        registerReceiver(networkStateBroadcastReceiver, filter)
    }

    protected fun setNoIntenetView(view: View?) {
        noInternetView = view
    }

    protected fun replaceFragment(fragment: Fragment) {
        val fragmentName: String = fragment::class.java.simpleName
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager
            .beginTransaction()
            .addToBackStack(fragmentName)
            .setCustomAnimations(
                R.animator.slide_in_left,
                R.animator.slide_out_right,
                R.animator.pop_out_right,
                R.animator.pop_in_left
            )
            .replace(R.id.content_container, fragment, fragmentName)
            .commit()
    }

    protected fun initializeToolbar(
        titleText: String? = null,
        enableHomeButton: Boolean = false, @DrawableRes iconRes: Int? = null
    ) {
        findViewById<Toolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener {
                onBackPressed()
                //                finishAfterTransition()
            }
            supportActionBar?.apply {
                title = titleText
                setDisplayHomeAsUpEnabled(enableHomeButton)
                iconRes?.let { setNavigationIcon(iconRes) }
            }
        }
    }

    protected fun setSubtitle(text: CharSequence) {
        supportActionBar?.subtitle = text
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_from_right, android.R.anim.fade_out)
    }

    override fun finish() {
        super.finish()
//        overridePendingTransition(R.anim.fade_in_short, R.anim.slide_out_to_right)
    }

    protected fun setToolbarTitle(title: CharSequence) {
        supportActionBar?.title = title
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val view = currentFocus
        when (view) {
            is EditText -> {
                val newTouchedView = findViewAt(view.rootView as ViewGroup, event.rawX.toInt(), event.rawY.toInt())
                if (newTouchedView is EditText || newTouchedView is ImageView) return super.dispatchTouchEvent(event)

                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) hideKeyboard()
            }
        }
        return super.dispatchTouchEvent(event)
    }


    override fun onStop() {
        unregisterReceiver(networkStateBroadcastReceiver)
        super.onStop()
    }

    private fun findViewAt(viewGroup: ViewGroup, x: Int, y: Int): View? {
        (0 until viewGroup.childCount)
            .map { viewGroup.getChildAt(it) }
            .forEach {
                when (it) {
                    is ViewGroup -> {
                        val foundView = findViewAt(it, x, y)
                        if (foundView?.isShown == true) return foundView
                    }
                    else -> {
                        val location = IntArray(2)
                        it.getLocationOnScreen(location)
                        val rect = Rect(location[0], location[1], location[0] + it.width, location[1] + it.height)
                        if (rect.contains(x, y)) return it
                    }
                }
            }
        return null
    }

    private fun hideKeyboard() = this.hideKeyboardEx()
}