package com.example.ecodigify.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ecodigify.R

class IngredientPairListFragmentListAdapter (private val dataSet: List<Pair<String, String>>, private val onClick: (Pair<String, String>) -> Unit) :
    RecyclerView.Adapter<IngredientPairListFragmentListAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        class ViewHolder(view: View, val onClick: (Pair<String, String>) -> Unit) : RecyclerView.ViewHolder(view) {
            // Define click listener for the ViewHolder's View
            val imageView: ImageView = view.findViewById(R.id.recipeFullImageView)
            val textView: TextView = view.findViewById(R.id.recipeFullTitleTextView)
            val recyclerView: RecyclerView = view.findViewById(R.id.recipeFullIngredientsRecyclerView)

            private var currentIngredientString: Pair<String, String>? = null

            init {
                itemView.setOnClickListener { currentIngredientString?.let { recipeFull -> onClick(recipeFull) } }
            }

            fun bind(ingredientString: Pair<String, String>) {
                currentIngredientString = ingredientString
                textView.text = ingredientString.second
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.ingredient_pair_list_row_item, viewGroup, false)

            return ViewHolder(view, onClick)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.bind(dataSet[position])

            // Get element from your dataset at this position and replace the
            // contents of the view with that element

            viewHolder.textView.text = dataSet[position].second
            if (true) // TODO: invoke manager to get actual presence of the ingredient
                viewHolder.textView.setTextColor(ContextCompat.getColor(viewHolder.textView.context, R.color.ingredient_missing))
            else
                viewHolder.textView.setTextColor(ContextCompat.getColor(viewHolder.textView.context, R.color.ingredient_present))
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size
}