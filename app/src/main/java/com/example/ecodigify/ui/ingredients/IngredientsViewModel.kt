package com.example.ecodigify.ui.ingredients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IngredientsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Ingredients Fragment"
    }
    val text: LiveData<String> = _text

    fun updateText(newText: String) {
        _text.value = newText
    }
}