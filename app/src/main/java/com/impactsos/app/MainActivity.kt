package com.sosimpact

import android.Manifest
import android.content.Intent
import android.hardware.*
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensorManager: SensorManager
    var accelerometer: Sensor? = null

    var impactThreshold = 1.0
    var impactDetected = false
    var impactTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 permissões
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE),
            1
        )

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val drawerLayout = DrawerLayout(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.BOTTOM
        layout.setPadding(50, 50, 50, 50)

        val button = Button(this)
        var isActive = false

        button.text = "SOS OFF"
        button.setBackgroundColor(android.graphics.Color.RED)
        button.setTextColor(android.graphics.Color.WHITE)

        val params = LinearLayout.LayoutParams(300, 300)
        params.gravity = Gravity.CENTER_HORIZONTAL
        button.layoutParams = params

        button.setOnClickListener {
            isActive = !isActive

            if (isActive) {
                button.text = "SOS ON"
                button.setBackgroundColor(android.graphics.Color.GREEN)
                sendSOS()
            } else {
                button.text = "SOS OFF"
                button.setBackgroundColor(android.graphics.Color.RED)
            }
        }

        layout.addView(button)

        // MENU
        val navView = NavigationView(this)
        val menu = navView.menu
        menu.add("Definições")

        navView.setNavigationItemSelectedListener {
            if (it.title == "Definições") {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            drawerLayout.closeDrawer(Gravity.RIGHT)
            true
        }

        val navParams = DrawerLayout.LayoutParams(
            DrawerLayout.LayoutParams.WRAP_CONTENT,
            DrawerLayout.LayoutParams.MATCH_PARENT
        )
        navParams.gravity = Gravity.RIGHT

        drawerLayout.addView(layout)
        drawerLayout.addView(navView, navParams)

        setContentView(drawerLayout)
    }

    private fun sendSOS() {
        val number = SettingsActivity.selectedNumber

        if (number == null) {
            Toast.makeText(this, "Escolhe um contacto primeiro!", Toast.LENGTH_SHORT).show()
            return
        }

        // 📩 SMS
        if (SettingsActivity.useSMS) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(
                    number,
                    null,
                    "🚨 SOS! Preciso de ajuda!",
                    null,
                    null
                )
                Toast.makeText(this, "SMS enviado!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Erro SMS", Toast.LENGTH_SHORT).show()
            }
        }

        // 📞 CHAMADA
        if (SettingsActivity.useCall) {
            try {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = android.net.Uri.parse("tel:$number")
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Erro chamada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt((x * x + y * y + z * z)) / SensorManager.GRAVITY_EARTH
        val currentTime = System.currentTimeMillis()

        if (gForce > impactThreshold && !impactDetected) {
            impactDetected = true
            impactTime = currentTime
        }

        if (impactDetected) {
            if (currentTime - impactTime > 3000) {
                if (gForce < 1.2) {
                    Toast.makeText(this, "🚨 POSSÍVEL ACIDENTE!", Toast.LENGTH_LONG).show()
                    sendSOS()
                }
                impactDetected = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
