package com.sosimpact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    companion object {
        var selectedNumber: String? = null
        var smsEnabled: Boolean = true
        var callEnabled: Boolean = false
    }

    val PICK_CONTACT = 1
    lateinit var contactText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 50, 50, 50)

        // 🔙 BOTÃO VOLTAR
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

        // ⚙️ SISTEMA SOS
        val sosTitle = TextView(this)
        sosTitle.text = "\n⚙️ Sistema SOS"
        layout.addView(sosTitle)

        // SMS SWITCH
        val smsSwitch = Switch(this)
        smsSwitch.text = "Enviar SMS"
        smsSwitch.isChecked = smsEnabled

        smsSwitch.setOnCheckedChangeListener { _, isChecked ->
            smsEnabled = isChecked
        }

        layout.addView(smsSwitch)

        // CHAMADA SWITCH
        val callSwitch = Switch(this)
        callSwitch.text = "Fazer chamada automática"
        callSwitch.isChecked = callEnabled

        callSwitch.setOnCheckedChangeListener { _, isChecked ->
            callEnabled = isChecked
        }

        layout.addView(callSwitch)

        // OUTRAS DEFINIÇÕES (mantive as tuas)
        val sensitivityText = TextView(this)
        sensitivityText.text = "\nSensibilidade do Impacto"

        val sensitivitySeek = SeekBar(this)
        sensitivitySeek.max = 20
        sensitivitySeek.progress = 10

        val timeText = TextView(this)
        timeText.text = "Tempo para ativar SOS (segundos)"

        val timeSeek = SeekBar(this)
        timeSeek.max = 10
        timeSeek.progress = 3

        layout.addView(sensitivityText)
        layout.addView(sensitivitySeek)
        layout.addView(timeText)
        layout.addView(timeSeek)

        setContentView(layout)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return

            val cursor = contentResolver.query(uri, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {

                val nameIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )

                val numberIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)

                // 🔥 GUARDA GLOBAL
                selectedNumber = number

                contactText.text = "Selecionado:\n$name\n$number"

                cursor.close()
            }
        }
    }
}
