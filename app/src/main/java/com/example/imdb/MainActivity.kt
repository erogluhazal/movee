package com.example.imdb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.imdb.R.layout
import com.example.imdb.login.LoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        var transaction = supportFragmentManager.beginTransaction()

        var fragment = LoginFragment()

        if (savedInstanceState == null) {
            transaction.replace(R.id.tvSeriesDetailLayout, fragment)
            transaction.commitAllowingStateLoss()
        }
    }

}
