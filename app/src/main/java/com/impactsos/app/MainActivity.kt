package com.sosimpact

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        val button = Button(this)
        button.text = "🚨 SOS"
        button.setOnClickListener {
            Toast.makeText(this, "🚨 SOS ATIVADO!", Toast.LENGTH_SHORT).show()
        }

        setContentView(button)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
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

        // Detect strong impact
        if (gForce > impactThreshold && !impactDetected) {
            impactDetected = true
            impactTime = currentTime
        }

        // Check if user stayed still after impact
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
