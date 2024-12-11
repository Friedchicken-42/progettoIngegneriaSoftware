package com.example.ecodigify.ui.ingredients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for the IngredientsFragment.
 *
 * This ViewModel holds and manages the data for the IngredientsFragment,
 * including the text displayed in the fragment. It provides a LiveData object
 * for observing changes to the text.
 */
class IngredientsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Ingredients Fragment"
    }
    val text: LiveData<String> = _text

    /**
     * Updates the text displayed in the fragment.
     *
     * @param newText The new text to display.
     */
    fun updateText(newText: String) {
        _text.value = newText
    }
}