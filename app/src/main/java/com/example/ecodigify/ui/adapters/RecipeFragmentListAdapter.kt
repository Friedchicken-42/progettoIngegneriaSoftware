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

/**
 * Adapter for displaying a list of [Recipe] in a RecyclerView.
 *
 * This adapter handles the creation and binding of ViewHolder objects for each
 * recipe in the dataset. It also manages the display of recipe information,
 * including the thumbnail image and name.
 *
 * @param dataSet The array of [Recipe] objects to display.
 * @param onClick A lambda function that is called when a recipe is clicked.
 */
class RecipeFragmentListAdapter(private val dataSet: Array<Recipe>, private val onClick: (Recipe) -> Unit) :
    RecyclerView.Adapter<RecipeFragmentListAdapter.ViewHolder>() {

    /**
     * ViewHolder for a recipe item in the RecyclerView.
     *
     * This class holds references to the views within the recipe item layout
     * and handles the display of recipe information.
     *
     * @param view The root view of the recipe item layout.
     * @param onClick A lambda function that is called when the recipe item is clicked.
     */
    class ViewHolder(view: View, val onClick: (Recipe) -> Unit) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.recipeImageView)
        val textView: TextView = view.findViewById(R.id.recipeTitleTextView)

        private var currentRecipe: Recipe? = null

        init {
            itemView.setOnClickListener { currentRecipe?.let { recipe -> onClick(recipe) } }
        }

        /**
         * Binds a [Recipe] object to the ViewHolder.
         *
         * This method updates the views within the ViewHolder to display the
         * information from the given recipe.
         *
         * @param recipe The recipe to bind.
         */
        fun bind(recipe: Recipe) {
            currentRecipe = recipe

            Glide.with(imageView.context)
                .load(recipe.thumbnail)
                .placeholder(R.drawable.ic_noimage_black_24dp)
                .error(R.drawable.ic_noimage_black_24dp)
                .into(imageView)
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
            .inflate(R.layout.recipe_row_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    /**
     * Binds a [Recipe] object to a ViewHolder.
     *
     * This method updates the contents of the ViewHolder to reflect the recipe
     * data, including loading the thumbnail image and setting the recipe name.
     *
     * @param viewHolder The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])

        Glide.with(viewHolder.imageView.context)
            .load(dataSet[position].thumbnail)
            .placeholder(R.drawable.ic_noimage_black_24dp)
            .error(R.drawable.ic_noimage_black_24dp)
            .into(viewHolder.imageView)
        viewHolder.textView.text = dataSet[position].name
        dataSet[position].thumbnail
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = dataSet.size
}
