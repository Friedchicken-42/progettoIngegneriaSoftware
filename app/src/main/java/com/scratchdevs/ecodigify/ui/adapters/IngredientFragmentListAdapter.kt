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
        val progressBar: ProgressBar = view.findViewById(R.id.ingredient_expiration_progress_bar)

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
        viewHolder.bind(dataSet[position])

        val daysSinceAdded: Float =
            Period.between(dataSet[position].addDate, LocalDate.now()).days.toFloat()
        val daysExpiration: Float = Period.between(
            dataSet[position].addDate,
            dataSet[position].expirationDate
        ).days.toFloat()
        val progress = if (LocalDate.now() > dataSet[position].expirationDate) {
            viewHolder.progressBar.max
        } else {
            min(
                ((daysSinceAdded + 1) / (daysExpiration + 1) * viewHolder.progressBar.max).toInt(),
                viewHolder.progressBar.max
            )
        }

        val context = viewHolder.itemView.context
        val progressColor = when {
            progress < 50 -> ContextCompat.getColor(context, R.color.progress_green)
            progress in 50..80 -> ContextCompat.getColor(context, R.color.progress_yellow)
            else -> ContextCompat.getColor(context, R.color.progress_red)
        }

        viewHolder.textView.text = dataSet[position].name
        viewHolder.progressBar.progress = progress
        viewHolder.progressBar.progressTintList = ColorStateList.valueOf(progressColor)
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
