package com.stgroup.enote.objects

import androidx.drawerlayout.widget.DrawerLayout
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.stgroup.enote.R
import com.stgroup.enote.utilities.APP_ACTIVITY

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
                PrimaryDrawerItem().withIdentifier(100).withName("Главная")
                    .withIcon(R.drawable.ic_home),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(200).withName("Корзина")
                    .withIcon(R.drawable.ic_rubbish),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(300).withName("Настройки")
                    .withIcon(R.drawable.ic_settings),
                SecondaryDrawerItem().withIdentifier(301).withName("Поделиться")
                    .withIcon(R.drawable.ic_share),
                DividerDrawerItem(),
                SecondaryDrawerItem().withIdentifier(400).withName("Версия").withBadge("0.1")
                    .withIcon(R.drawable.ic_info).withEnabled(false)
            )
            .build()
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