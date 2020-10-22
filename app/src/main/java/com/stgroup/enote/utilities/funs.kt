package com.stgroup.enote.utilities
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.stgroup.enote.MainActivity
import com.stgroup.enote.R

fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
    //Use addToBackStack true if you want to add fragment to stack
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
fun restartActivity(){
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)

    APP_ACTIVITY.finish()
}