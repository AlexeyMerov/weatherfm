package com.alexeymerov.weatherfm.utils.extensions

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64
import java.security.MessageDigest
import java.util.*

@Suppress("DEPRECATION")
fun String.fromHtml(): Spanned {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        else -> Html.fromHtml(this)
    }
}

fun String.toMd5(): String = hashWithAlgorithm("MD5")
fun String.toBase64(): String = toByteArray().toBase64()
fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.DEFAULT)
fun String.fromBase64() = String(Base64.decode(this, Base64.DEFAULT))

private fun String.hashWithAlgorithm(algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm)
    val bytes = digest.digest(toByteArray(Charsets.UTF_8))
    return bytes.fold("") { str, it -> str + "%02x".format(it) }
}

fun getRandomId() = UUID.randomUUID().toString()

fun String.setBoldSpannable(
    textToBold: String = "",
    startIndexIndent: Int = 0,
    endIndexIndent: Int = 0
): SpannableString {
    val spannableContent = SpannableString(this)
    var startPosition = if (textToBold.isNotEmpty()) this.indexOf(textToBold) else 0
    var endPosition = if (textToBold.isNotEmpty()) startPosition.plus(textToBold.length) else this.length - 1
    if (startIndexIndent > 0 && startPosition.minus(startIndexIndent) >= 0) startPosition =
            startPosition.minus(startIndexIndent)
    if (endIndexIndent > 0 && endPosition.plus(endIndexIndent) <= this.length - 1) endPosition =
            endPosition.plus(endIndexIndent)
    spannableContent.setSpan(StyleSpan(Typeface.BOLD), startPosition, endPosition, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    return spannableContent
}

fun String.setBlackSpannable(afterWhichChar: String): SpannableString {
    val spannableContent = SpannableString(this)
    if (!contains(afterWhichChar)) return spannableContent

    val charIndex = indexOf(afterWhichChar) + 1
    spannableContent.setSpan(ForegroundColorSpan(Color.BLACK), charIndex, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    return spannableContent
}

fun generateString(
    count: Int = 10,
    useSmall: Boolean = false,
    useNumbers: Boolean = false,
    useSymbols: Boolean = false
): String {
    val latinChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val latinSmallChars = "abcdefghijklmnopqrstuvwxyz"
    val numbersChars = "0123456789"
    val symbolsChars = "!@#$%^&*()_+~=-\"№;:?.,][}{\'`\\/|"
    var stringWithChars = latinChars
    if (useSmall) stringWithChars = stringWithChars.plus(latinSmallChars)
    if (useNumbers) stringWithChars = stringWithChars.plus(numbersChars)
    if (useSymbols) stringWithChars = stringWithChars.plus(symbolsChars)
    var resultString = ""
    0.until(count).forEach {
        resultString = resultString.plus(latinChars[Random().nextInt(stringWithChars.length - 1)].toString())
    }
    return resultString
}
