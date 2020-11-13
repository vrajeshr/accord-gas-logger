package com.example.accordapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.jaredrummler.android.shell.CommandResult
import com.jaredrummler.android.shell.Shell


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("HELLO WORLD")
        println(Shell.SU.available())
    }
}