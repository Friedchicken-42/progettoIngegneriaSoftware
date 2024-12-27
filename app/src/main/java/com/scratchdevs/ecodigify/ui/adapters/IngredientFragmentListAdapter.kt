package com.scratchdevs.ecodigify.ui.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.scratchdevs.ecodigify.R
import com.scratchdevs.ecodigify.dataclass.Ingredient
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import kotlin.math.min

/**
 * Adapter for displaying a list of [Ingredient] in a RecyclerView.
 *
 * This adapter handles the creation and binding of ViewHolder objects for each
 * ingredient in the dataset. It also manages the display of ingredient information,
 * including the name and expiration progress.
 *
 * @param dataSet The array of [Ingredient] objects to display.
 * @param onClick A lambda function that is called when an ingredient is clicked.
 */
class IngredientFragmentListAdapter(
    private val dataSet: Array<Ingredient>,
    private val onClick: (Ingredient) -> Unit
) :
    androidx.recyclerview.widget.RecyclerView.Adapter<IngredientFragmentListAdapter.ViewHolder>() {

    /**
     * ViewHolder for an ingredient item in the RecyclerView.
     *
     * This class holds references to the views within the ingredient item layout
     * and handles the display of ingredient information.
     *
     * @param view The root view of the ingredient item layout.
     * @param onClick A lambda function that is called when the ingredient item is clicked.
     */
    class ViewHolder(
        view: View,
        val onClick: (Ingredient) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val textView: TextView = view.findViewById(R.id.ingredient_title_text_view)
        val quantityView: TextView = view.findViewById(R.id.ingredient_quantity_text)
        val dateView: TextView = view.findViewById(R.id.ingredient_days_until_expiration)

        private var currentIngredient: Ingredient? = null

        init {
            itemView.setOnClickListener { currentIngredient?.let { ingredient -> onClick(ingredient) } }
        }

        /**
         * Binds an [Ingredient] object to the ViewHolder.
         *
         * This method updates the views within the ViewHolder to display the
         * information from the given ingredient.
         *
         * @param ingredient The ingredient to bind.
         */
        fun bind(ingredient: Ingredient) {
            currentIngredient = ingredient
            textView.text = ingredient.name
        }
    }

    /**
     * Creates a new ViewHolder instance.
     *
     * This method is called by the RecyclerView when it needs a new ViewHolder
     * to represent an ingredient item.
     *
     * @param viewGroup The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder instance.
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.ingredient_row_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    /**
     * Binds an [Ingredient] object to a ViewHolder.
     *
     * This method is called by the RecyclerView to display the data at the
     * specified position. It updates the contents of the ViewHolder to reflect
     * the ingredient data.
     *
     * @param viewHolder The ViewHolder which should be updated to represent
     * the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.itemView.context
        val ingredient = dataSet[position]
        viewHolder.bind(ingredient)

        viewHolder.quantityView.text = ingredient.quantity
        viewHolder.textView.text = ingredient.name

        val daysExpiration = ChronoUnit.DAYS.between(LocalDate.now(), ingredient.expirationDate)

        viewHolder.dateView.text = when {
            daysExpiration < 0 -> context.getString(R.string.ingredient_expired)
            daysExpiration == 0L -> context.getString(R.string.ingredient_expiring)
            else -> context.getString(R.string.ingredient_expiration_report, daysExpiration)
        }

        if (daysExpiration < 0) {
            viewHolder.dateView.setTextColor(ContextCompat.getColor(context, R.color.progress_red))
        } else if (daysExpiration == 0L) {
            viewHolder.dateView.setTextColor(ContextCompat.getColor(context, R.color.progress_yellow))
        }

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * This method is called by the RecyclerView to determine how many items
     * to display.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = dataSet.size
}