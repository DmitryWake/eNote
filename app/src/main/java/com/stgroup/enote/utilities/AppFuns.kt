package com.stgroup.enote.utilities
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.stgroup.enote.MainActivity
import com.stgroup.enote.R
import java.text.DateFormatSymbols
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Calendar

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

fun getFormattedCurrentDate() : String {

    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val date = LocalDateTime.now()
        val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM kk:mm")
        date.format(formatter)
    }
    else {
        val cal = Calendar.getInstance().apply { time = Date() }
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = DateFormatSymbols().months[cal.get(Calendar.MONTH)]
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        "$day $month $hour:$minute"
    }

}