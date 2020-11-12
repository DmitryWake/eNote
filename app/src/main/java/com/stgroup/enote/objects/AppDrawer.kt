package com.stgroup.enote.objects

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.stgroup.enote.R
import com.stgroup.enote.screens.RubbishFragment
import com.stgroup.enote.screens.SettingsFragment
import com.stgroup.enote.screens.main_menu_screen.MainMenuFragment
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.showToast
import com.stgroup.enote.utilities.replaceFragment


class AppDrawer {

    private lateinit var mDrawer: Drawer
    private lateinit var mHeader: AccountHeader
    private lateinit var mDrawerLayout: DrawerLayout

    fun create() {
        createHeader()
        createDrawer()
        mDrawerLayout = mDrawer.drawerLayout
    }

    private fun createHeader() {
        mHeader = AccountHeaderBuilder().withActivity(APP_ACTIVITY)
            .withHeaderBackground(R.drawable.header).build()
    }

    private fun createDrawer() {
        mDrawer = DrawerBuilder().withActivity(APP_ACTIVITY).withToolbar(APP_ACTIVITY.mToolbar)
            .withActionBarDrawerToggle(true)
            .withAccountHeader(mHeader)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(100).withName(R.string.home_icon)
                    .withIcon(R.drawable.ic_home),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(200).withName(R.string.trash_icon)
                    .withIcon(R.drawable.ic_rubbish),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(300).withName(R.string.settings_icon)
                    .withIcon(R.drawable.ic_settings),
                SecondaryDrawerItem().withIdentifier(301).withName(R.string.share_icon)
                    .withIcon(R.drawable.ic_share),
                DividerDrawerItem(),
                SecondaryDrawerItem().withIdentifier(400).withName(R.string.version_icon).withBadge(R.string.app_version)
                    .withIcon(R.drawable.ic_info).withEnabled(false)
            ).withOnDrawerItemClickListener(object: Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view : View?, position : Int, drawerItem : IDrawerItem<*>) : Boolean
                {
                    chooseItem(position)
                    return false
                }
            })
            .build()
    }

    private fun chooseItem(position : Int) {

        when (position) {
            1 -> replaceFragment(MainMenuFragment())
            //3 -> replaceFragment(RubbishFragment())
            5 -> replaceFragment(SettingsFragment())
            6 -> APP_ACTIVITY.showToast("Sharing is good!")
        }
    }

    fun disableDrawer() {
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.mToolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }

    fun enableDrawer() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.mToolbar.setNavigationOnClickListener {
            mDrawer.openDrawer()
        }
    }

}