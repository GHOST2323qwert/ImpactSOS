package com.sosimpact

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this)
        text.text = "⚙️ Definições\n\nModo escuro (em breve)"
        text.textSize = 20f

        setContentView(text)
    }
}
