package net.store.divineit.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun String?.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this ?: "").matches()
}

fun String?.isValidPassword(): Boolean {
    val password = this ?: ""
    return password.isNotBlank() && password.length >= AppConstants.PASSWORD_VALID_LENGTH
}

fun String?.toShortForm(): String {
    var result = "N/A"
    if (this.isNullOrBlank()) return result
    val words = this.split(" ")
    val stringBuilder = StringBuilder()
    for (word in words) {
        if (word.isNotBlank() && word.trim()[0].toString().matches("[A-Za-z]".toRegex()))
            stringBuilder.append(word[0])
    }
    result = stringBuilder.toString()
    return result
}

val String.colorValue
    get() = Color.parseColor(this)

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    if (currentFocus == null) {
        hideKeyboard(View(this))
    } else {
        hideKeyboard(currentFocus as View)
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
}