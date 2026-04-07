package com.sosimpact

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 50, 50, 50)

        // Sensibilidade
        val sensitivityText = TextView(this)
        sensitivityText.text = "Sensibilidade do Impacto"

        val sensitivitySeek = SeekBar(this)
        sensitivitySeek.max = 20
        sensitivitySeek.progress = 10

        // Tempo
        val timeText = TextView(this)
        timeText.text = "Tempo para ativar SOS (segundos)"

        val timeSeek = SeekBar(this)
        timeSeek.max = 10
        timeSeek.progress = 3

        // Som
        val soundSwitch = Switch(this)
        soundSwitch.text = "Som de Alerta"
        soundSwitch.isChecked = true

        // Localização
        val locationSwitch = Switch(this)
        locationSwitch.text = "Enviar Localização"
        locationSwitch.isChecked = true

        layout.addView(sensitivityText)
        layout.addView(sensitivitySeek)

        layout.addView(timeText)
        layout.addView(timeSeek)

        layout.addView(soundSwitch)
        layout.addView(locationSwitch)

        setContentView(layout)
    }
}
