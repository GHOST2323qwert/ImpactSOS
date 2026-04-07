package com.sosimpact

import android.content.Intent
import android.hardware.*
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val drawerLayout = DrawerLayout(this)

        // 🔴 MAIN CONTENT
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 200, 50, 50)

        val sosButton = Button(this)
        sosButton.text = "🚨 SOS"

        sosButton.setOnClickListener {
            Toast.makeText(this, "SOS ACTIVATED!", Toast.LENGTH_SHORT).show()
        }

        val openMenu = Button(this)
        openMenu.text = "☰ Menu"

        openMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.RIGHT)
        }

        layout.addView(openMenu)
        layout.addView(sosButton)

        // 📌 MENU LATERAL
        val navView = NavigationView(this)

        val menu = navView.menu
        menu.add("Definições")
        menu.add("Sobre a App")

        navView.setNavigationItemSelectedListener {
            when (it.title) {
               // "Definições" -> startActivity(Intent(this, SettingsActivity::class.java))
                //"Sobre a App" -> startActivity(Intent(this, AboutActivity::class.java))
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
                    Toast.makeText(this, "🚨 POSSÍVEL ACIDENTE DETETADO!", Toast.LENGTH_LONG).show()
                }
                impactDetected = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
