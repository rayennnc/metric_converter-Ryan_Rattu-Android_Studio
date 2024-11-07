package com.example.metricconverter

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {
    private lateinit var spinnerMetric: Spinner
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextValue: EditText
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerMetric = findViewById(R.id.spinnerMetric)
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        editTextValue = findViewById(R.id.editTextValue)
        textViewResult = findViewById(R.id.textViewResult)

        // Siapkan spinner untuk metrik
        ArrayAdapter.createFromResource(
            this,
            R.array.metrics,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMetric.adapter = adapter
        }

        // Matikan spinner unit 
        spinnerFrom.isEnabled = false
        spinnerTo.isEnabled = false

        spinnerMetric.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    1 -> setupUnitSpinners(R.array.length_units)
                    2 -> setupUnitSpinners(R.array.mass_units)
                    3 -> setupUnitSpinners(R.array.time_units)
                    4 -> setupUnitSpinners(R.array.current_units)
                    5 -> setupUnitSpinners(R.array.temperature_units)
                    6 -> setupUnitSpinners(R.array.luminous_units)
                    7 -> setupUnitSpinners(R.array.substance_units)
                    else -> {
                        spinnerFrom.isEnabled = false
                        spinnerTo.isEnabled = false
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Siapkan listener untuk konversi
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                convert()
            }
        }
        editTextValue.addTextChangedListener(textWatcher)
    }

    private fun setupUnitSpinners(unitsArrayId: Int) {
        ArrayAdapter.createFromResource(
            this,
            unitsArrayId,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFrom.adapter = adapter
            spinnerTo.adapter = adapter
        }

        spinnerFrom.isEnabled = true
        spinnerTo.isEnabled = true

        val unitListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convert()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerFrom.onItemSelectedListener = unitListener
        spinnerTo.onItemSelectedListener = unitListener
    }

    private fun convert() {
        if (editTextValue.text.toString().isEmpty()) {
            textViewResult.text = ""
            return
        }

        try {
            val value = editTextValue.text.toString().toDouble()
            val result = when (spinnerMetric.selectedItemPosition) {
                1 -> convertLength(value, spinnerFrom.selectedItemPosition, spinnerTo.selectedItemPosition)
                2 -> convertMass(value, spinnerFrom.selectedItemPosition, spinnerTo.selectedItemPosition)
                3 -> convertTime(value, spinnerFrom.selectedItemPosition, spinnerTo.selectedItemPosition)
                4 -> convertCurrent(value, spinnerFrom.selectedItemPosition, spinnerTo.selectedItemPosition)
                5 -> convertTemperature(value, spinnerFrom.selectedItemPosition, spinnerTo.selectedItemPosition)
                6 -> convertLuminousIntensity(value, spinnerFrom.selectedItemPosition, spinnerTo.selectedItemPosition)
                7 -> convertSubstance(value, spinnerFrom.selectedItemPosition, spinnerTo.selectedItemPosition)
                else -> value
            }
            textViewResult.text = String.format("%.2f", result)
        } catch (e: NumberFormatException) {
            textViewResult.text = getString(R.string.invalid_input)
        }
    }

    private fun convertLength(value: Double, fromUnit: Int, toUnit: Int): Double {
        // Ubah ke meter 
        var inMeters = value
        when (fromUnit) {
            0 -> inMeters = value * 1000 // Kilometer
            2 -> inMeters = value / 100 // Sentimeter
            3 -> inMeters = value / 1000 // Milimeter
        }

        return when (toUnit) {
            0 -> inMeters / 1000 // Ke Kilometer
            1 -> inMeters // Ke Meter
            2 -> inMeters * 100 // Ke Sentimeter
            3 -> inMeters * 1000 // Ke Milimeter
            else -> inMeters
        }
    }

    private fun convertMass(value: Double, fromUnit: Int, toUnit: Int): Double {
        // Ubah ke gram 
        var inGrams = value
        when (fromUnit) {
            0 -> inGrams = value * 1000 // Kilogram ke gram
            2 -> inGrams = value / 1000 // Miligram ke gram
            3 -> inGrams = value / 1000000 // Mikrogram ke gram
        }

        return when (toUnit) {
            0 -> inGrams / 1000 // Ke Kilogram
            1 -> inGrams // Ke Gram
            2 -> inGrams * 1000 // Ke Miligram
            3 -> inGrams * 1000000 // Ke Mikrogram
            else -> inGrams
        }
    }

    private fun convertTime(value: Double, fromUnit: Int, toUnit: Int): Double {
        // Ubah ke detik 
        var inSeconds = value
        when (fromUnit) {
            0 -> inSeconds = value * 3600 // Jam ke detik
            1 -> inSeconds = value * 60 // Menit ke detik
            3 -> inSeconds = value / 1000 // Milidetik ke detik
        }

        return when (toUnit) {
            0 -> inSeconds / 3600 // Ke Jam
            1 -> inSeconds / 60 // Ke Menit
            2 -> inSeconds // Ke Detik
            3 -> inSeconds * 1000 // Ke Milidetik
            else -> inSeconds
        }
    }

    private fun convertCurrent(value: Double, fromUnit: Int, toUnit: Int): Double {
        // Ubah ke ampere 
        var inAmpere = value
        when (fromUnit) {
            1 -> inAmpere = value / 1000 // Miliampere ke Ampere
            2 -> inAmpere = value / 1000000 // Mikroampere ke Ampere
            3 -> inAmpere = value * 1000 // Kiloampere ke Ampere
        }

        return when (toUnit) {
            0 -> inAmpere // Ke Ampere
            1 -> inAmpere * 1000 // Ke Miliampere
            2 -> inAmpere * 1000000 // Ke Mikroampere
            3 -> inAmpere / 1000 // Ke Kiloampere
            else -> inAmpere
        }
    }

    private fun convertTemperature(value: Double, fromUnit: Int, toUnit: Int): Double {
        // Ubah ke kelvin
        var inKelvin = value
        when (fromUnit) {
            1 -> inKelvin = value + 273.15 // Celsius ke Kelvin
            2 -> inKelvin = (value + 459.67) * 5/9 // Fahrenheit ke Kelvin
        }

        return when (toUnit) {
            0 -> inKelvin // Ke Kelvin
            1 -> inKelvin - 273.15 // Ke Celsius
            2 -> inKelvin * 9/5 - 459.67 // Ke Fahrenheit
            else -> inKelvin
        }
    }

    private fun convertLuminousIntensity(value: Double, fromUnit: Int, toUnit: Int): Double {
        // Ubah ke kandela
        var inCandela = value
        when (fromUnit) {
            1 -> inCandela = value / 1000 // Milikandela ke Kandela
            2 -> inCandela = value * 1000 // Kilokandela ke Kandela
        }

        return when (toUnit) {
            0 -> inCandela // Ke Kandela
            1 -> inCandela * 1000 // Ke Milikandela
            2 -> inCandela / 1000 // Ke Kilokandela
            else -> inCandela
        }
    }

    private fun convertSubstance(value: Double, fromUnit: Int, toUnit: Int): Double {
        // Ubah ke mol
        var inMole = value
        when (fromUnit) {
            1 -> inMole = value / 1000 // Milimol ke Mol
            2 -> inMole = value / 1000000 // Mikromol ke Mol
            3 -> inMole = value * 1000 // Kilomol ke Mol
        }

        return when (toUnit) {
            0 -> inMole // Ke Mol
            1 -> inMole * 1000 // Ke Milimol
            2 -> inMole * 1000000 // Ke Mikromol
            3 -> inMole / 1000 // Ke Kilomol
            else -> inMole
        }
    }
}
