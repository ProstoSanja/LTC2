package com.thatguyalex.ltc2.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.appbar.MaterialToolbar
import com.thatguyalex.ltc2.R
import com.thatguyalex.ltc2.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.topAppBar))

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<LoginFragment>(R.id.fragment_container_view)
        }
    }
}