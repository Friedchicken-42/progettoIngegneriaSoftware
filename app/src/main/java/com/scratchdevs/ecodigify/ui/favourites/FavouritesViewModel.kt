package com.scratchdevs.ecodigify.ui.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for the FavouritesFragment.
 *
 * This ViewModel holds and manages the data for the FavouritesFragment,
 * including the text displayed in the fragment. It provides a LiveData object
 * for observing changes to the text.
 */
class FavouritesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is favourites Fragment"
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