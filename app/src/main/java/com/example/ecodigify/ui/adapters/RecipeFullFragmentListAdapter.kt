package com.example.ecodigify.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.dataclass.RecipeFull

/**
 * Enum representing the display mode for ingredients in a recipe.
 */
enum class DisplayIngredients {
    Hide,
    Display,
}

/**
 * Adapter for displaying a list of [RecipeFull] in a RecyclerView.
 *
 * This adapter handles the creation and binding of ViewHolder objects for each
 * full recipe in the dataset. It also manages the display of recipe information,
 * including the thumbnail image, name, and ingredients (if enabled).
 *
 * @param dataSet The array of [RecipeFull] objects to display.
 * @param ingredients The array of [Ingredient] objects representing the user's inventory.
 * @param display The display mode for ingredients (show or hide).
 * @param onClick A lambda function that is called when a recipe is clicked.
 */
class RecipeFullFragmentListAdapter(
    private val dataSet: Array<RecipeFull>,
    private val ingredients: Array<Ingredient>,
    private val display: DisplayIngredients,
    private val onClick: (RecipeFull) -> Unit
) :
    RecyclerView.Adapter<RecipeFullFragmentListAdapter.ViewHolder>() {

    /**
     * ViewHolder for a full recipe item in the RecyclerView.
     *
     * This class holds references to the views within the recipe item layout
     * and handles the display of recipe information.
     *
     * @param view The root view of the recipe item layout.
     * @param onClick A lambda function that is called when the recipe item is clicked.
     */
    class ViewHolder(view: View, val onClick: (RecipeFull) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.recipe_full_image_view)
        val textView: TextView = view.findViewById(R.id.recipe_full_title_text_view)
        var recyclerView: RecyclerView = view.findViewById(R.id.recipe_full_ingredients_recycler_view)

        private var currentRecipeFull: RecipeFull? = null

        init {
            itemView.setOnClickListener { currentRecipeFull?.let { recipeFull -> onClick(recipeFull) } }
        }

        /**
         * Binds a [RecipeFull] object to the ViewHolder.
         *
         * This method updates the views within the ViewHolder to display the
         * information from the given recipe.
         *
         * @param recipeFull The recipe to bind.
         */
        fun bind(recipeFull: RecipeFull) {
            currentRecipeFull = recipeFull

            textView.text = recipeFull.name

            Glide.with(imageView.context)
                .load(recipeFull.thumbnail)
                .placeholder(R.drawable.ic_noimage_black_24dp)
                .error(R.drawable.ic_noimage_black_24dp)
                .into(imageView)

            recyclerView.layoutManager = LinearLayoutManager(itemView.context)
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
            .inflate(R.layout.recipefull_row_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    /**
     * Binds a [RecipeFull] object to a ViewHolder.
     *
     * This method updates the contents of the ViewHolder to reflect the recipe
     * data, including loading the thumbnail image, setting the recipe name, and
     * setting up the ingredients RecyclerView (if enabled).
     *
     * @param viewHolder The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])

        if (display == DisplayIngredients.Hide) return

        viewHolder.textView.text = dataSet[position].name

        viewHolder.recyclerView.adapter = IngredientPairListFragmentListAdapter(
            dataSet[position].ingredients,
            ingredients,
            display,
        ) { }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = dataSet.size
}
