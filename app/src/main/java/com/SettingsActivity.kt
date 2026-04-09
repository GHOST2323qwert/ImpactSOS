package com.sosimpact

import android.app.Activity
import android.content.Context
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
        var impactValue: Float = 2.5f
    }

    val PICK_CONTACT = 1
    lateinit var contactText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("SOS_PREFS", Context.MODE_PRIVATE)

        // carregar dados
        selectedNumber = prefs.getString("number", null)
        useSMS = prefs.getBoolean("sms", true)
        useCall = prefs.getBoolean("call", false)
        sendLocation = prefs.getBoolean("location", true)
        impactValue = prefs.getFloat("impact", 2.5f)

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
        contactText.text = selectedNumber ?: "Nenhum contacto selecionado"

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
        val title = TextView(this)
        title.text = "⚙️ Sistema SOS"
        layout.addView(title)

        val smsSwitch = Switch(this)
        smsSwitch.text = "Enviar SMS"
        smsSwitch.isChecked = useSMS
        smsSwitch.setOnCheckedChangeListener { _, isChecked ->
            useSMS = isChecked
            prefs.edit().putBoolean("sms", isChecked).apply()
        }

        val callSwitch = Switch(this)
        callSwitch.text = "Chamada automática"
        callSwitch.isChecked = useCall
        callSwitch.setOnCheckedChangeListener { _, isChecked ->
            useCall = isChecked
            prefs.edit().putBoolean("call", isChecked).apply()
        }

        val locationSwitch = Switch(this)
        locationSwitch.text = "Enviar Localização"
        locationSwitch.isChecked = sendLocation
        locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sendLocation = isChecked
            prefs.edit().putBoolean("location", isChecked).apply()
        }

        layout.addView(smsSwitch)
        layout.addView(callSwitch)
        layout.addView(locationSwitch)

        // ⚡ IMPACTO
        val impactInput = EditText(this)
        impactInput.hint = "Valor de impacto (G)"
        impactInput.setText(prefs.getFloat("impact", 2.5f).toString())

        impactInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val value = impactInput.text.toString().toFloatOrNull()
                if (value != null) {
                    impactValue = value
                    prefs.edit().putFloat("impact", value).apply()
                }
            }
        }

        layout.addView(impactInput)

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

                    // ✅ CORREÇÃO AQUI
                    val prefs = getSharedPreferences("SOS_PREFS", Context.MODE_PRIVATE)
                    prefs.edit().putString("number", number).apply()

                    contactText.text = "Selecionado:\n$name\n$number"
                }
            }
        }
    }
}
