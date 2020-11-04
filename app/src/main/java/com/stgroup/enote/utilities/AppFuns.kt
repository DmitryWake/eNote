package com.stgroup.enote.utilities
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.stgroup.enote.MainActivity
import com.stgroup.enote.R

fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
    // Use addToBackStack true if you want to add fragment to stack
    if (addToBackStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.data_container, fragment).addToBackStack(null).commit()
        APP_ACTIVITY.mDrawer.disableDrawer()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.data_container, fragment).commit()
    }
}

// Method to make a toast. Use it with your context
fun Context.showToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


// Restart activity function
fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)

    APP_ACTIVITY.finish()
}

// Прячем клавиатуру :)
fun hideKeyboard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

// Инициализация локальных хранилищ
fun initLocalBase() {
    CATEGORIES_STORAGE = APP_ACTIVITY.getSharedPreferences(
        STORAGE_CATEGORIES_NAME, Context.MODE_PRIVATE
    )
    NOTES_STORAGE = APP_ACTIVITY.getSharedPreferences(
        STORAGE_NOTES_NAME, Context.MODE_PRIVATE
    )
}

// Потом автоматизировать
fun getThemeTextColour(themeName: String): Int =
    when (themeName) {
        "NightSky" -> ContextCompat.getColor(APP_ACTIVITY, R.color.colorAccent)
        else -> ContextCompat.getColor(APP_ACTIVITY, R.color.colorBlack)
    }