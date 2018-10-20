package com.alexeymerov.weatherfm.utils.extensions

import android.animation.Animator
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

fun View.showToast(text: Any) = Toast.makeText(this.context, text.toString(), Toast.LENGTH_SHORT).show()

inline fun View.showSnack(message: String, isLong: Boolean = false, f: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(
        this,
        "<font color=\"#549bdf\">$message</font>".fromHtml(),
        if (isLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT
    )
    snack.f()
    snack.show()
}

inline fun EditText.textWatcher(f: EditTextWatcher.() -> Unit) = addTextChangedListener(EditTextWatcher().apply(f))

class EditTextWatcher : TextWatcher {

    private var before: (String.() -> Unit)? = null
    private var onChanged: (String.() -> Unit)? = null
    private var after: (String.() -> Unit?)? = null

    fun before(f: String.() -> Unit) {
        before = f
    }

    fun onChanged(f: String.() -> Unit = {}) {
        onChanged = f
    }

    fun after(f: String.() -> Unit = {}) {
        after = f
    }

    override fun afterTextChanged(s: Editable?) {
        s?.toString()?.apply { after?.invoke(this) }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        s?.toString()?.apply { before?.invoke(this) }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.toString()?.apply { onChanged?.invoke(this) }
    }
}

fun View.hideDown() {
    if (this.isVisible()) {
        animate().translationY(height.toFloat()).alpha(0f).duration = 175
        makeVisible(false)
    }
}

fun View.showUp() {
    if (!this.isVisible()) {
        makeVisible()
        animate().translationYBy(-height.toFloat()).alpha(1f).duration = 175
    }
}

fun View.showDown() {
    animate()
        .translationYBy(-height.toFloat())
        .setDuration(175)
        .addAnimatorListener {
            onStart { this@showDown.makeVisible() }
        }
}

fun View.hideUp() {
    animate()
        .translationY(height.toFloat())
        .setDuration(175)
        .addAnimatorListener {
            onEnd { this@hideUp.makeVisible(false) }
        }
}

fun View.fadeIn() {
    animate().alpha(1f)
        .setDuration(175)
        .addAnimatorListener {
            onStart { this@fadeIn.makeVisible() }
        }
}

fun View.fadeOut() {
    animate().alpha(0f).setDuration(175)
        .addAnimatorListener {
            onEnd { this@fadeOut.makeVisible(false) }
        }
}

inline fun ViewPropertyAnimator.addAnimatorListener(f: AnimatorListener.() -> Unit): ViewPropertyAnimator =
    setListener(AnimatorListener().apply(f))

class AnimatorListener : Animator.AnimatorListener {
    private var onStart: ((AnimatorListener) -> Unit)? = null
    private var onRepeat: ((AnimatorListener) -> Unit)? = null
    private var onEnd: ((AnimatorListener) -> Unit)? = null
    private var onCancel: ((AnimatorListener) -> Unit?)? = null


    fun AnimatorListener.onStart(f: AnimatorListener.() -> Unit) {
        onStart = f
    }

    fun AnimatorListener.onRepeat(f: AnimatorListener.() -> Unit = {}) {
        onRepeat = f
    }

    fun AnimatorListener.onEnd(f: AnimatorListener.() -> Unit = {}) {
        onEnd = f
    }

    fun AnimatorListener.onCancel(f: AnimatorListener.() -> Unit = {}) {
        onEnd = f
    }

    override fun onAnimationRepeat(animation: Animator?) {
        onRepeat?.invoke(this)
    }

    override fun onAnimationEnd(animation: Animator?) {
        onEnd?.invoke(this)
    }

    override fun onAnimationCancel(animation: Animator?) {
        onCancel?.invoke(this)
    }

    override fun onAnimationStart(animation: Animator?) {
        onStart?.invoke(this)
    }
}

fun View.isVisible() = visibility == VISIBLE

fun View.playAnimation(@AnimRes resId: Int, onEnd: () -> Unit = {}) {
    startAnimation(AnimationUtils.loadAnimation(this.context, resId).apply {
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                onEnd.invoke()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    })
}

fun ViewGroup.inflate(layoutRes: Int): View = LayoutInflater.from(context).inflate(layoutRes, this, false)

fun View.setCircleColor(colorHexString: String) = setRoundedBackground(Color.parseColor(colorHexString))

fun View.setRoundedBackground(color: Int) {
    when (this.background) {
        is GradientDrawable -> (this.background as GradientDrawable).setColor(color)
        else -> this.background.setColorFilter(color, PorterDuff.Mode.OVERLAY)
    }
}

fun View.changeMargin(top: Int = -1, bottom: Int = -1, start: Int = -1, end: Int = -1) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val param = layoutParams as ViewGroup.MarginLayoutParams
        param.marginStart = if (start == -1) param.marginStart else start.dpToPx()
        param.topMargin = if (top == -1) param.topMargin else top.dpToPx()
        param.marginEnd = if (end == -1) param.marginEnd else end.dpToPx()
        param.bottomMargin = if (bottom == -1) param.bottomMargin else bottom.dpToPx()
        layoutParams = param
    }
}

fun View.changePadding(top: Int = -1, bottom: Int = -1, start: Int = -1, end: Int = -1) {
    val finalStartPadding = if (start == -1) this.paddingStart else start.dpToPx()
    val finalTopPadding = if (top == -1) this.paddingTop else top.dpToPx()
    val finalEndPadding = if (end == -1) this.paddingEnd else end.dpToPx()
    val finalBottomPadding = if (bottom == -1) this.paddingBottom else bottom.dpToPx()
    this.setPadding(finalStartPadding, finalTopPadding, finalEndPadding, finalBottomPadding)
}

fun View.changeSize(width: Int = -1, height: Int = -1) {
    if (layoutParams is LinearLayout.LayoutParams) {
        val param = layoutParams as LinearLayout.LayoutParams
        param.width = if (width == -1) param.width else width.dpToPx()
        param.height = if (height == -1) param.height else height.dpToPx()
        layoutParams = param
    }
}

fun getRotateAnimation() = RotateAnimation(
    0.0f, 360.0f,
    Animation.RELATIVE_TO_SELF, 0.5f,
    Animation.RELATIVE_TO_SELF, 0.5f
)
    .apply {
        interpolator = LinearInterpolator()
        repeatCount = -1
        duration = 1000
    }

fun RecyclerView.setScrollListenerOnResizeActivity(layoutManager: LinearLayoutManager): View.OnLayoutChangeListener {
    return View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
        val index = layoutManager.itemCount - 1
        if (bottom < oldBottom && index > 0) {
            this@setScrollListenerOnResizeActivity.post {
                this@setScrollListenerOnResizeActivity.scrollToPosition(layoutManager.itemCount - 1)
            }
        }
    }
}

fun View.makeVisible(visible: Boolean = true) {
    visibility = if (visible) View.VISIBLE else View.GONE
}