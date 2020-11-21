package com.example.accordapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jaredrummler.android.shell.Shell
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.sql.Timestamp


var AWS_URL = "https://6w0byurle5.execute-api.us-east-1.amazonaws.com/prod/accord-lambda"
var CSV_FILE_NAME = "gas_log.csv"

class MainActivity : AppCompatActivity() {
    lateinit var odometerInput: EditText;
    lateinit var gallonsInput: EditText;
    lateinit var pricePerGallon: EditText;
    lateinit var total: EditText;

    lateinit var submitButton: Button;
    lateinit var sharedPref: SharedPreferences;

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
            override fun afterTextChanged(s: Editable) {
                calculateTotal()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        pricePerGallon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                calculateTotal()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        submitButton.setOnClickListener {
            onSubmit()
        }
    }

    fun onSubmit() {
//        if( odometerInput.text.isEmpty() || pricePerGallon.text.isEmpty() || gallonsInput.text.isEmpty() || total.text.isEmpty() ){
//            return
//        }
//
//        var line = getCSVLine();
//        resetFields();
//
//        var file = File(getExternalFilesDir("AccordApp"), CSV_FILE_NAME)
//
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (e: Exception) {
//                println("File failed to write: ${e.toString()}")
//            }
//        }
//
//        try {
//            file.appendText(line);
//        } catch (e: Exception) {
//            println("File I/O Error: $e");
//        }
//
//        var fileContent = StringBuilder()
//
//        file.forEachLine {
//            fileContent.append(it + "\n")
//        }
//
//        sendPOST(fileContent.toString());
        sendPOST("Hello world");
    }

    fun isSUAvailable(): Boolean {
        return Shell.SU.available();
    }

    fun calculateTotal() {
        if (pricePerGallon.text.length != 0 && gallonsInput.text.length != 0) {
            var price = (pricePerGallon.text.toString()).toDouble()
            var gallons = (gallonsInput.text.toString()).toDouble()

            var totalCost = price * gallons;

            total.setText(totalCost.toString());
        }
    }

    fun resetFields(){
        odometerInput.setText("");
        pricePerGallon.setText("");
        gallonsInput.setText("")
        total.setText("")
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

    fun sendPOST(fileContent: String) {
        val thread = Thread(Runnable {
            try {
                val jsonParam = JSONObject()
                jsonParam.put("data", fileContent)

                val url = URL(AWS_URL)
//                var url = URL("http://127.0.0.1:5000/")

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type","application/json");

                    var wr = DataOutputStream(this.outputStream);
                    wr.writeBytes(jsonParam.toString());
                    wr.flush();
                    wr.close();

//                    DisplayToast(responseCode);

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLIne = it.readLine()
                        while(inputLIne != null) {
                            response.append(inputLIne)
                            inputLIne = it.readLine()
                        }

                        it.close()

                        System.out.println(response);
                    }
                }
            } catch (e: java.lang.Exception) {
                System.out.println(e.toString());
            }
        })
        thread.start();
    }
}