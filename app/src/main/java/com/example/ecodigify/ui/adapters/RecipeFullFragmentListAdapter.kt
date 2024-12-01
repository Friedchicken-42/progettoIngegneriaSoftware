package com.example.ecodigify.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.dataclass.Recipe
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.ui.popup.PopupIngredientsActivity
import com.example.ecodigify.ui.popup.PopupRecipeActivity

class RecipeFullFragmentListAdapter(private val dataSet: Array<RecipeFull>, private val onClick: (RecipeFull) -> Unit) :
    RecyclerView.Adapter<RecipeFullFragmentListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */

    class ViewHolder(view: View, val onClick: (RecipeFull) -> Unit) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val imageView: ImageView = view.findViewById(R.id.recipeFullImageView)
        val textView: TextView = view.findViewById(R.id.recipeFullTitleTextView)
        var recyclerView: RecyclerView = view.findViewById(R.id.recipeFullIngredientsRecyclerView)

        private var currentRecipeFull: RecipeFull? = null

        init {
            itemView.setOnClickListener { currentRecipeFull?.let { recipeFull -> onClick(recipeFull) } }
        }

        fun bind(recipeFull: RecipeFull) {
            currentRecipeFull = recipeFull

            textView.text = recipeFull.name

            Glide.with(imageView.context)
                .load(recipeFull.thumbnail)
                .placeholder(R.drawable.ic_noimage_black_24dp)
                .error(R.drawable.ic_noimage_black_24dp)
                .into(imageView)

            recyclerView.adapter = IngredientPairListFragmentListAdapter(
                recipeFull.ingredients
            ){}
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recipefull_row_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.textView.text = dataSet[position].name
        Glide.with(viewHolder.imageView.context)
            .load(dataSet[position].source)
            .placeholder(R.drawable.ic_noimage_black_24dp)
            .error(R.drawable.ic_noimage_black_24dp)
            .into(viewHolder.imageView)

        viewHolder.recyclerView.adapter = IngredientPairListFragmentListAdapter(
            dataSet[position].ingredients
        ) { }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
