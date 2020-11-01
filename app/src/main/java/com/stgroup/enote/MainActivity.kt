package com.stgroup.enote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.stgroup.enote.databinding.ActivityMainBinding
import com.stgroup.enote.objects.AppDrawer
import com.stgroup.enote.screens.main_menu_screen.MainMenuFragment
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.replaceFragment
import com.stgroup.enote.utilities.showToast

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mToolbar: Toolbar
    lateinit var mDrawer: AppDrawer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initFields()
        initFunc()

        // Метод работает Oh-yeah
        this.showToast("Заходит как-то Обама в бар")
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mDrawer = AppDrawer()
        APP_ACTIVITY = this
    }

    private fun initFunc() {
        setSupportActionBar(mToolbar)
        mDrawer.create()
        replaceFragment(MainMenuFragment(), false)
    }
}

// Мама, я в телевизоре (с) Артур