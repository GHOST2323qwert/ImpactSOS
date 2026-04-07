package com.sosimpact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    val PICK_CONTACT = 1
    lateinit var contactText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 50, 50, 50)

        // 🔙 Botão voltar
        val backButton = Button(this)
        backButton.text = "← Voltar"
        backButton.setOnClickListener {
            finish()
        }
        layout.addView(backButton)

        // 📞 CONTACTO
        val contactButton = Button(this)
        contactButton.text = "Escolher Contacto"

        contactText = TextView(this)
        contactText.text = "Nenhum contacto selecionado"

        contactButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            )
            startActivityForResult(intent, PICK_CONTACT)
        }

        layout.addView(contactButton)
        layout.addView(contactText)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            val cursor = contentResolver.query(uri!!, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {

                val nameIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )
                val numberIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)

                contactText.text = "Selecionado:\n$name\n$number"

                cursor.close()
            }
        }
    }
}

