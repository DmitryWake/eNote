package com.stgroup.enote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.stgroup.enote.databinding.ActivityMainBinding
import com.stgroup.enote.objects.AppDrawer
import com.stgroup.enote.utilities.APP_ACTIVITY

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
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mDrawer = AppDrawer()
        APP_ACTIVITY = this
    }

    private fun initFunc() {
        setSupportActionBar(mToolbar)
        mDrawer.create()
    }
}

// Мама, я в телевизоре (с) Артур