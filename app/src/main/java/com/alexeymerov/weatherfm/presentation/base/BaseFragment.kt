package com.alexeymerov.weatherfm.presentation.base

import android.app.ActionBar
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.alexeymerov.weatherfm.utils.extensions.hideKeyboardEx
import com.alexeymerov.weatherfm.utils.extensions.showSnack
import com.alexeymerov.weatherfm.utils.extensions.showToast
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseFragment : Fragment() {

    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun runDisposable(f: () -> Disposable) {
        compositeDisposable.add(f.invoke())
    }

    protected fun <T> singleTransformer(): SingleTransformer<T, T> = SingleTransformer {
        it
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private val appBar: ActionBar? = activity?.actionBar

    protected fun disableHomeAsUp() = appBar?.setDisplayHomeAsUpEnabled(false)

    protected fun initializeNavigationBar(title: String, showBackButton: Boolean, @DrawableRes resId: Int) {
        appBar?.apply {
            setTitle(title)
            setDisplayHomeAsUpEnabled(showBackButton)
            setHomeAsUpIndicator(resId)
            elevation = 4f
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> fragmentManager?.popBackStackImmediate()
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun showToast(text: String) = activity?.showToast(text)

    protected fun showSnack(text: String) = activity?.showSnack(text)

    protected fun hideKeyboard() = activity?.hideKeyboardEx()

    override fun onDestroyView() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
        super.onDestroyView()
    }
}
