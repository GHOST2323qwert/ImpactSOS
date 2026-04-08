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
        var sendLocation: Boolean = true
        var useSMS: Boolean = true
        var useCall: Boolean = false
    }

    val PICK_CONTACT = 1
    lateinit var contactText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 50, 50, 50)

        // 🔙 voltar
        val backButton = Button(this)
        backButton.text = "← Voltar"
        backButton.setOnClickListener { finish() }
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
        sosTitle.text = "⚙️ Sistema SOS"
        layout.addView(sosTitle)

        val smsSwitch = Switch(this)
        smsSwitch.text = "Enviar SMS"
        smsSwitch.isChecked = true
        smsSwitch.setOnCheckedChangeListener { _, isChecked ->
            useSMS = isChecked
        }

        val callSwitch = Switch(this)
        callSwitch.text = "Fazer chamada automática"
        callSwitch.isChecked = false
        callSwitch.setOnCheckedChangeListener { _, isChecked ->
            useCall = isChecked
        }

        val locationSwitch = Switch(this)
        locationSwitch.text = "Enviar Localização"
        locationSwitch.isChecked = true
        locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sendLocation = isChecked
        }

        layout.addView(smsSwitch)
        layout.addView(callSwitch)
        layout.addView(locationSwitch)

        setContentView(layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            val cursor = contentResolver.query(uri, null, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )
                    val numberIndex = it.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )

                    val name = it.getString(nameIndex)
                    val number = it.getString(numberIndex)

                    selectedNumber = number
                    contactText.text = "Selecionado:\n$name\n$number"
                }
            }
        }
    }
}
