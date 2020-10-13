package com.stgroup.enote.utilities
import androidx.fragment.app.Fragment
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