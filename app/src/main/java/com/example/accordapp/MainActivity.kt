package com.example.accordapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jaredrummler.android.shell.Shell
import java.io.File
import java.sql.Time
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {
    final var fileName = "data.csv"

    lateinit var odometerInput: EditText;
    lateinit var gallonsInput: EditText;
    lateinit var pricePerGallon: EditText;
    lateinit var total: EditText;

    lateinit var submitButton: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        odometerInput = findViewById(R.id.odometerInput);
        gallonsInput = findViewById(R.id.gallonsInput);
        pricePerGallon = findViewById(R.id.pricePerGallonInput);
        total = findViewById(R.id.totalInput);
        submitButton = findViewById(R.id.submitButton);


        gallonsInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) { calculateTotal() }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        pricePerGallon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) { calculateTotal() }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        submitButton.setOnClickListener {
            onSubmit()
        }
    }

    fun isSUAvailable(): Boolean {
        return Shell.SU.available();
    }

    fun calculateTotal() {
        if( pricePerGallon.text.length != 0 && gallonsInput.text.length !=0 ){
            var price = (pricePerGallon.text.toString()).toDouble()
            var gallons = (gallonsInput.text.toString()).toDouble()

            var totalCost = price * gallons;

            total.setText(totalCost.toString());
        }
    }

    fun getCSVLine(): String {
        var line = ""
        var localTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {

        };
        line += "$localTime,"
        line += "${odometerInput.text.toString()},"
        line += "${pricePerGallon.text.toString()},"
        line += "${gallonsInput.text.toString()},"
        line += "${total.text.toString()},"

        return line
    }

    fun onSubmit() {
        val file = File(this.filesDir, fileName)
        if( !file.exists() ){
            file.createNewFile()
        }

        var line = getCSVLine()

        file.writeText(line)
    }
}