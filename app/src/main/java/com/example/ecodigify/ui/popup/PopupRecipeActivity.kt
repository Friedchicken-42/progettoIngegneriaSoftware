package com.example.ecodigify.ui.popup

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.ecodigify.Manager
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.run

/**
 * Activity for displaying recipe details in a popup window.
 *
 * This activity allows the user to view the details of a recipe, including
 * its image, title, servings, source, ingredients, and instructions. It also
 * provides functionality for adding or removing the recipe from the user's
 * favorites.
 */
class PopupRecipeActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_popup_recipe)

        // Set padding for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get UI elements
        val recipeImageView = findViewById<ImageView>(R.id.singleRecipeImageView)
        val titleTextView = findViewById<TextView>(R.id.singleRecipeTitleTextView)
        val servingsTextView = findViewById<TextView>(R.id.servingsTextView)
        val sourceButton = findViewById<TextView>(R.id.sourceButton)
        val favoriteImageView = findViewById<ImageView>(R.id.favorite_icon)
        val ingredientInstructionTextView = findViewById<TextView>(R.id.editTextTextMultiLine)

        // Get recipe data from intent
        @Suppress("DEPRECATION")
        val recipe = intent.getParcelableExtra<RecipeFull>("RECIPE")

        // Set up source button listener
        sourceButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, recipe?.source)
            startActivity(intent)
        }


        // Set up favorite icon and listener
        var fav = true
        run(
            lifecycle = lifecycle,
            callback = { Manager.recipeGet(recipe!!) }, // TODO: recipe could be null(?)
            done = { r ->
                fav = r != null
                favoriteImageView.setImageResource(if (fav) R.drawable.ic_favorite_red_24dp else R.drawable.ic_favorite_black_24dp)
            },
        )

        favoriteImageView.setOnClickListener {
            fav = !fav
            favoriteImageView.setImageResource(if (fav) R.drawable.ic_favorite_red_24dp else R.drawable.ic_favorite_black_24dp)
            run(
                lifecycle = lifecycle,
                callback = {
                    if (!fav) {
                        Manager.recipeRemove(recipe!!)
                    } else {
                        Manager.recipeAdd(recipe!!)
                    }
                }, done = { }
            )
        }

        // Display recipe details
        recipe?.let {
            Glide.with(recipeImageView.context)
                .load(recipe.thumbnail)
                .placeholder(R.drawable.ic_noimage_black_24dp)
                .error(R.drawable.ic_noimage_black_24dp)
                .into(recipeImageView)
            titleTextView.text = recipe.name

            servingsTextView.text = ""
            val formattedIngredients =
                recipe.ingredients.joinToString("\n") { (name, quantity) -> "$name: $quantity" }
            ingredientInstructionTextView.text = getString(
                R.string.single_recipe_format_string,
                formattedIngredients,
                recipe.instructions
            )


        }

    }
}