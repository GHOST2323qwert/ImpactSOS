package com.impactsos.app

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val button = Button(this)
        button.text = "🚨 SOS"

        button.setOnClickListener {
            Toast.makeText(this, "SOS ACTIVATED!", Toast.LENGTH_SHORT).show()
        }

        setContentView(button)
    }
}
