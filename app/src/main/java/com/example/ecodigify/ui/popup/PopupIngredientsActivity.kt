package com.example.ecodigify.ui.popup

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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

        var possibleNamesWithAdd: MutableList<String> = arrayListOf()

        ingredient?.let {
            possibleNamesWithAdd = it.possibleNames.toMutableList()
            possibleNamesWithAdd.add(getString(R.string.add_new_name_text))
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, possibleNamesWithAdd)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            altNameSpinner.adapter = adapter
            val currentNameIndex = possibleNamesWithAdd.indexOf(ingredient?.name)
            if (currentNameIndex != -1) {
                altNameSpinner.setSelection(currentNameIndex)
            }

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            dateButton.text = it.expirationDate.format(formatter)

            val quantityRange = 1..12
            quantityPicker.minValue = quantityRange.first
            quantityPicker.maxValue = quantityRange.last
            quantityPicker.wrapSelectorWheel = false
            val currentQuantity = it.quantity.toIntOrNull()?.let { quantity ->
                if (quantity > quantityRange.last) quantityRange.first else quantity
            } ?: quantityRange.first

            quantityPicker.value = currentQuantity
        }

        altNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (pos == possibleNamesWithAdd.size - 1) { // if new item has been selected
                    showAddNewItemDialog()
                } else {
                    val selectedName = parent.getItemAtPosition(pos).toString()
                    ingredient = ingredient?.copy(name = selectedName)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No change
            }

            private fun showAddNewItemDialog() {
                val builder = AlertDialog.Builder(altNameSpinner.context)
                builder.setTitle(getString(R.string.add_new_name_title_text))

                val input = EditText(altNameSpinner.context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setPositiveButton(getString(R.string.apply_button_text)) { dialog, _ ->
                    val newItemName = input.text.toString()
                    if (newItemName.isNotBlank()) {
                        ingredient?.possibleNames = ingredient?.possibleNames?.plus(listOf(newItemName))!!

                        runOnUiThread {
                            val adapter = altNameSpinner.adapter as ArrayAdapter<String>
                            adapter.clear()
                            adapter.addAll(ingredient?.possibleNames ?: emptyList())
                            adapter.notifyDataSetChanged()
                            altNameSpinner.setSelection(adapter.count - 1)
                        }
                    }
                }
                builder.setNegativeButton(getString(R.string.cancel_button_text)) { dialog, _ -> dialog.cancel() }

                builder.show()
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
                     callback = {
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
