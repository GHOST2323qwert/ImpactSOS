package com.sosimpact

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this)
        text.text = "ℹ️ ImpactSOS\nVersão 1.0\nApp de deteção de acidentes"
        text.textSize = 20f

        setContentView(text)
    }
}
