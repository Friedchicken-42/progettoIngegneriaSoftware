package com.example.ecodigify.ui.popup

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.Ingredient
import java.time.format.DateTimeFormatter


class PopupIngredientsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_popup_ingredients)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val altNameSpinner = findViewById<Spinner>(R.id.altNameSpinner)
        val dateButton = findViewById<Button>(R.id.dateButton)
        val quantityPicker = findViewById<NumberPicker>(R.id.quantityPicker)

        val ingredient = intent.getParcelableExtra<Ingredient>("INGREDIENT")

        ingredient?.let {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it.possible_names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            altNameSpinner.adapter = adapter

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            dateButton.text = it.expiration.format(formatter)

            val quantityRange = 0..100
            quantityPicker.minValue = quantityRange.first
            quantityPicker.maxValue = quantityRange.last
            quantityPicker.wrapSelectorWheel = false
            val currentQuantity = it.quantity.toIntOrNull() ?: 0
            quantityPicker.value = currentQuantity
        }

    }
    // TODO: implement data saves on apply button click
}