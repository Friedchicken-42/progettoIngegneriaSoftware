package com.example.ecodigify.ui.popup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.emptyLongSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.ecodigify.R
import com.example.ecodigify.dataclass.RecipeFull


class PopupRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_popup_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recipeImageView = findViewById<ImageView>(R.id.singleRecipeImageView)
        val titleTextView = findViewById<TextView>(R.id.singleRecipeTitleTextView)
        val servingsTextView = findViewById<TextView>(R.id.servingsTextView)
        val sourceButton = findViewById<TextView>(R.id.sourceButton)
        val favoriteImageView = findViewById<ImageView>(R.id.favorite_icon)
        val ingredientInstructionTextView = findViewById<TextView>(R.id.editTextTextMultiLine)

        val recipe = intent.getParcelableExtra<RecipeFull>("RECIPE")

        sourceButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, recipe?.source)
            startActivity(intent)
        }

        var fav: Boolean = true // TODO: get from manager if recipe is in favourites

        favoriteImageView.setOnClickListener {
            fav = !fav
            favoriteImageView.setImageResource( if (fav) R.drawable.ic_favorite_red_24dp else R.drawable.ic_favorite_black_24dp);
            // TODO: commit changes to manager
        }

        recipe?.let {
            Glide.with(recipeImageView.context)
                .load(recipe.thumbnail)
                .placeholder(R.drawable.ic_noimage_black_24dp)
                .error(R.drawable.ic_noimage_black_24dp)
                .into(recipeImageView)
            titleTextView.text = recipe.name
            servingsTextView.text = "99 piatti in fila per sei con il resto di 2" // TODO: fix with actual servings
            val formattedIngredients = recipe.ingredients.joinToString("\n") { (first, second) -> second }
            ingredientInstructionTextView.text = getString(R.string.single_recipe_format_string, formattedIngredients, recipe.instructions)


        }

    }
}

/*
* val id: Int,
    val name: String,
    val thumbnail: Uri,
    val instructions: String,
    val ingredients: List<Pair<String, String>>,
    val source: Uri,
* */