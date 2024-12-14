package com.example.ecodigify.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.Ingredient

/**
 * Adapter for displaying a list of ingredient pairs (ingredient and quantity) in
 * a RecyclerView.
 *
 * This adapter handles the creation and binding of ViewHolder objects for each
 * ingredient pair in the dataset. It also manages the display of ingredient
 * information, including the name, quantity, and whether the ingredient is present
 * in the user's inventory.
 *
 * @param dataSet The list of ingredient pairs to display.
 * @param ingredients The array of [Ingredient] objects representing the user's inventory.
 * @param display The display mode for ingredients (show or hide).
 * @param onClick A lambda function that is called when an ingredient pair is clicked.
 */
class IngredientPairListFragmentListAdapter(
    private val dataSet: List<Pair<String, String>>,
    private val ingredients: Array<Ingredient>,
    private val display: DisplayIngredients,
    private val onClick: (Pair<String, String>) -> Unit
) :
    RecyclerView.Adapter<IngredientPairListFragmentListAdapter.ViewHolder>() {

    /**
     * ViewHolder for an ingredient pair item in the RecyclerView.
     *
     * This class holds references to the views within the ingredient pair item layout
     * and handles the display of ingredient information.
     *
     * @param view The root view of the ingredient pair item layout.
     * @param onClick A lambda function that is called when the ingredient pair item is clicked.
     */
    class ViewHolder(view: View, val onClick: (Pair<String, String>) -> Unit) :
        RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.ingredient_pair_text_view)

        private var currentIngredientString: Pair<String, String>? = null

        init {
            itemView.setOnClickListener {
                currentIngredientString?.let { recipeFull ->
                    onClick(
                        recipeFull
                    )
                }
            }
        }

        /**
         * Binds an ingredient pair to the ViewHolder.
         *
         * This method updates the views within the ViewHolder to display the
         * information from the given ingredient pair.
         *
         * @param ingredientString The ingredient pair to bind.
         */
        fun bind(ingredientString: Pair<String, String>) {
            currentIngredientString = ingredientString

            textView.text = textView.context.getString(
                R.string.required_ingredient_format_string,
                ingredientString.first,
                ingredientString.second
            )
        }
    }

    /**
     * Creates a new ViewHolder instance.
     *
     * @param viewGroup The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder instance.
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.ingredient_pair_list_row_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    /**
     * Binds an ingredient pair to a ViewHolder.
     *
     * This method updates the contents of the ViewHolder to reflect the ingredient
     * pair data, including highlighting ingredients present in the user's inventory.
     *
     * @param viewHolder The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])

        if (display == DisplayIngredients.Hide) return

        val name = dataSet[position].first

        viewHolder.textView.text = viewHolder.textView.context.getString(
            R.string.required_ingredient_format_string,
            dataSet[position].first,
            dataSet[position].second
        )

        if (ingredients.any { i -> i.name.lowercase() == name.lowercase() })
            viewHolder.textView.setTextColor(
                ContextCompat.getColor(
                    viewHolder.textView.context,
                    R.color.ingredient_present
                )
            )
        else
            viewHolder.textView.setTextColor(
                ContextCompat.getColor(
                    viewHolder.textView.context,
                    R.color.ingredient_missing
                )
            )
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = dataSet.size
}