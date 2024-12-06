package com.example.ecodigify.ui.popup

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecodigify.Manager
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.run
import java.time.LocalDate
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
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val applyButton = findViewById<Button>(R.id.applyButton)

        var ingredient = intent.getParcelableExtra<Ingredient>("INGREDIENT")
        val oldIngredient = ingredient

        ingredient?.let {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it.possibleNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            altNameSpinner.adapter = adapter

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            dateButton.text = it.expirationDate.format(formatter)

            val quantityRange = 0..100
            quantityPicker.minValue = quantityRange.first
            quantityPicker.maxValue = quantityRange.last
            quantityPicker.wrapSelectorWheel = false
            val currentQuantity = it.quantity.toIntOrNull() ?: 0
            quantityPicker.value = currentQuantity
        }

        altNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selectedName = parent.getItemAtPosition(pos).toString()
                ingredient = ingredient?.copy(name = selectedName)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No change
            }
        }

        dateButton.setOnClickListener {
            val currentDate =
                LocalDate.parse(dateButton.text, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            showDatePickerDialog(currentDate) { selectedDate ->
                dateButton.text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ingredient = ingredient?.copy(expirationDate = selectedDate)
            }
        }

        quantityPicker.setOnValueChangedListener { picker, oldValue, newValue ->
            ingredient =
                ingredient?.copy(quantity = newValue.toString()) // TODO: check if this is correct (what about the 1/3 cup...)
        }

        cancelButton.setOnClickListener {
            finish()
        }

        applyButton.setOnClickListener {
            oldIngredient?.let { it ->
                run(
                    lifecycle = lifecycle,
                    function = {
                        Manager.ingredientRemove(it)
                        Manager.ingredientAdd(ingredient!!)
                    },
                    done = {}
                )
            }

            finish()
        }
    }

    private fun showDatePickerDialog(currentDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentDate.year)
            set(Calendar.MONTH, currentDate.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, currentDate.dayOfMonth)
        }

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate =
                    LocalDate.of(year, month + 1, dayOfMonth) // select the following day
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
