package com.example.ecodigify

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecodigify.dataclass.RecipeFull

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
        val recyclerView: RecyclerView = view.findViewById(R.id.recipeFullIngredientsRecyclerView)

        private var currentRecipeFull: RecipeFull? = null

        init {
            itemView.setOnClickListener { currentRecipeFull?.let { recipeFull -> onClick(recipeFull) } }
        }

        fun bind(recipeFull: RecipeFull) {
            currentRecipeFull = recipeFull
            textView.text = recipeFull.name
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
        // TODO: populate recycler view
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
