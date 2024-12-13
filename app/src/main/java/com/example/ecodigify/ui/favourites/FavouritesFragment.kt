package com.example.ecodigify.ui.favourites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecodigify.Manager
import com.example.ecodigify.R
import com.example.ecodigify.databinding.FragmentFavouritesBinding
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.run
import com.example.ecodigify.ui.adapters.DisplayIngredients
import com.example.ecodigify.ui.adapters.RecipeFullFragmentListAdapter
import com.example.ecodigify.ui.popup.PopupRecipeActivity

/**
 * Fragment for displaying the user's favorite recipes.
 *
 * This fragment shows a list of the user's favorite recipes, allowing them to
 * view details and interact with each recipe. It uses a RecyclerView to display
 * the recipes and handles navigation to a detailed recipe view when a recipe
 * is clicked.
 */
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    /**
     * Creates the view for the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return The View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val favouritesViewModel =
            ViewModelProvider(this)[FavouritesViewModel::class.java]

        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFavourites
        favouritesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { display() }

        return root
    }

    /**
     * Called immediately after onCreateView() has returned, giving subclasses a
     * chance to initialize themselves once they have access to their view hierarchy.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.favouritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up OnItemTouchListener to handle nested RecyclerView scrolling
        binding.favouritesRecyclerView.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val viewChild = rv.findChildViewUnder(e.x, e.y)
                val ingredientsRecyclerView =
                    viewChild?.findViewById<RecyclerView>(R.id.recipe_full_ingredients_recycler_view)

                if (ingredientsRecyclerView != null) {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            val canScrollVertically =
                                ingredientsRecyclerView.canScrollVertically(-1) || ingredientsRecyclerView.canScrollVertically(
                                    1
                                )
                            rv.requestDisallowInterceptTouchEvent(canScrollVertically)
                        }

                        MotionEvent.ACTION_UP -> rv.requestDisallowInterceptTouchEvent(false)
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        display()
    }

    /**
     * Called when the view previously created by onCreateView() is detached from
     * the fragment.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Handles clicks on recipe items in the adapter.
     *
     * This method starts the PopupRecipeActivity with the selected recipe.
     *
     * @param recipeFull The [RecipeFull] object representing the clicked recipe.
     */
    private fun adapterOnClick(recipeFull: RecipeFull) {
        val intent = Intent(binding.root.context, PopupRecipeActivity()::class.java)
        intent.putExtra("RECIPE", recipeFull)
        activityResultLauncher.launch(intent)
    }

    /**
     * Displays the favorite recipes in the RecyclerView.
     *
     * This method retrieves the recipes and ingredients from the manager and
     * sets up the adapter for the RecyclerView. It also updates the ViewModel
     * text based on whether there are any favorite recipes.
     */
    private fun display() {
        run(
            lifecycle = lifecycle,
            callback = { Pair(Manager.recipeGetAll(), Manager.ingredientGetAll()) },
            done = { (recipes, ingredients) ->
                binding.favouritesRecyclerView.adapter =
                    RecipeFullFragmentListAdapter(
                        recipes.toTypedArray(),
                        ingredients.toTypedArray(),
                        DisplayIngredients.Display
                    ) { ing -> adapterOnClick(ing) }

                val favouritesViewModel =
                    ViewModelProvider(this)[FavouritesViewModel::class.java]

                favouritesViewModel.updateText(
                    if (recipes.isEmpty()) {
                        requireView().context.getString(R.string.no_recipes_text)
                    } else {
                        ""
                    }
                )
            }
        )
    }
}