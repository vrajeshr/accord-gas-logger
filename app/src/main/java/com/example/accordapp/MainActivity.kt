package com.example.accordapp

import android.R.attr.name
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.jaredrummler.android.shell.Shell
import java.io.File
import java.net.URL
import java.sql.Timestamp


var AWS_URL = "https://6w0byurle5.execute-api.us-east-1.amazonaws.com/prod/accord-lambda"
var CSV_FILE_NAME = "gas_log.csv"

class MainActivity : AppCompatActivity() {
    lateinit var odometerInput: EditText;
    lateinit var gallonsInput: EditText;
    lateinit var pricePerGallon: EditText;
    lateinit var total: EditText;

    lateinit var submitButton: Button;
    lateinit var sharedPref:SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        odometerInput = findViewById(R.id.odometerInput);
        gallonsInput = findViewById(R.id.gallonsInput);
        pricePerGallon = findViewById(R.id.pricePerGallonInput);
        total = findViewById(R.id.totalInput);
        submitButton = findViewById(R.id.submitButton);

        sharedPref = this?.getPreferences(Context.MODE_PRIVATE)


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


    fun onSubmit() {
        var line = getCSVLine();

        var file = File(getExternalFilesDir("AccordApp"), CSV_FILE_NAME)

        if( !file.exists() ){
            try{
                file.createNewFile();
            }
            catch ( e : Exception ){
                println("File failed to write: ${e.toString()}")
            }
        }

        try {
            file.appendText(line);
        }
        catch( e: Exception) {
            println("File I/O Error: $e");
        }
        sendPOST();
    }

    fun getCSVLine(): String {
        var line = ""

        var now = Timestamp(System.currentTimeMillis());

        line += "$now,"
        line += "${odometerInput.text},"
        line += "${pricePerGallon.text},"
        line += "${gallonsInput.text},"
        line += "${total.text}\n"

        return line
    }

    fun sendPOST(){
        val url = URL(AWS_URL)

    }

}