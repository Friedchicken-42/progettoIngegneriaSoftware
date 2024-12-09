package com.example.ecodigify.ui.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.Ingredient
import java.time.LocalDate
import java.time.Period
import kotlin.math.min

class IngredientFragmentListAdapter(private val dataSet: Array<Ingredient>, private val onClick: (Ingredient) -> Unit) :
    RecyclerView.Adapter<IngredientFragmentListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View, val onClick: (Ingredient) -> Unit) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val textView: TextView = view.findViewById(R.id.ingredientTitleTextView)
        val progressBar: ProgressBar = view.findViewById(R.id.ingredientExpirationProgressBar)

        private var currentIngredient: Ingredient? = null

        init {
            itemView.setOnClickListener { currentIngredient?.let { ingredient -> onClick(ingredient) } }
        }

        fun bind(ingredient: Ingredient) {
            currentIngredient = ingredient
            textView.text = ingredient.name
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.ingredient_row_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        val daysSinceAdded: Float = Period.between(dataSet[position].addDate, LocalDate.now()).days.toFloat()
        val daysExpiration: Float =Period.between(dataSet[position].addDate, dataSet[position].expirationDate).days.toFloat()
        val progress: Int = min(((daysSinceAdded + 1) / (daysExpiration + 1) * viewHolder.progressBar.max).toInt(), viewHolder.progressBar.max)

        val context = viewHolder.itemView.context
        val progressColor = when {
            progress < 50 -> ContextCompat.getColor(context, R.color.progress_green)
            progress in 50..80 -> ContextCompat.getColor(context, R.color.progress_yellow)
            else -> ContextCompat.getColor(context, R.color.progress_red)
        }

        // Update the progress color dynamically
        viewHolder.textView.text = dataSet[position].name
        viewHolder.progressBar.progress = progress
        viewHolder.progressBar.progressTintList = ColorStateList.valueOf(progressColor)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}