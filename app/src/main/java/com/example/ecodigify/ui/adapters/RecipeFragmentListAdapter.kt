package com.example.ecodigify.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.Recipe

class RecipeFragmentListAdapter(private val dataSet: Array<Recipe>, private val onClick: (Recipe) -> Unit) :
    RecyclerView.Adapter<RecipeFragmentListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View, val onClick: (Recipe) -> Unit) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val imageView: ImageView = view.findViewById(R.id.recipeImageView)
        val textView: TextView = view.findViewById(R.id.recipeTitleTextView)

        private var currentRecipe: Recipe? = null

        init {
            itemView.setOnClickListener { currentRecipe?.let { recipe -> onClick(recipe) } }
        }

        fun bind(recipe: Recipe) {
            currentRecipe = recipe

            Glide.with(imageView.context)
                .load(recipe.thumbnail) // Assuming thumbnail is of type Uri
                .placeholder(R.drawable.ic_noimage_black_24dp) // Optional placeholder
                .error(R.drawable.ic_noimage_black_24dp) // Optional error image
                .into(imageView)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recipe_row_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        // viewHolder.imageView // TODO: implement image retrival
        viewHolder.textView.text = dataSet[position].name
        dataSet[position].thumbnail
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
