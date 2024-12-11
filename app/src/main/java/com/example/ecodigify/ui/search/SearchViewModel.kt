package com.example.ecodigify.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for the SearchFragment.
 *
 * This ViewModel holds and manages the data for the SearchFragment,
 * including the text displayed in the fragment. It provides a LiveData object
 * for observing changes to the text.
 */
class SearchViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is search Fragment"
    }

    /**
     * LiveData object for observing the text displayed in the fragment.
     */
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