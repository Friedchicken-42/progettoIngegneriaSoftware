package com.example.ecodigify.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecodigify.Manager
import com.example.ecodigify.R
import com.example.ecodigify.databinding.FragmentSearchBinding
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.run
import com.example.ecodigify.ui.adapters.DisplayIngredients
import com.example.ecodigify.ui.adapters.RecipeFullFragmentListAdapter
import com.example.ecodigify.ui.popup.PopupRecipeActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

/**
 * Fragment for searching and displaying recipes.
 *
 * This fragment allows the user to search for recipes by name or ingredients
 * and displays the results in a RecyclerView. It also provides filtering
 * options to refine the search results.
 */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var searchJob: Job? = null

    // TODO: switch search
    private var search: String = "cake"

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
        val searchViewModel =
            ViewModelProvider(this)[SearchViewModel::class.java]

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSearch
        searchViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

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
        super.onViewCreated(view, savedInstanceState)

        val searchView: SearchView = binding.searchView
        val filterButton: ImageButton = binding.filterButton
        val unwantedIngredients: MutableList<Int> = mutableListOf()

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up OnItemTouchListener to handle nested RecyclerView scrolling
        binding.recipeRecyclerView.addOnItemTouchListener(object :
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

        // Set up search view listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                this.onQueryTextChange(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchJob?.cancel()
                    searchJob = run(
                        lifecycle = lifecycle,
                        callback = {
                            delay(250) // Debounce
                            search = newText
                            Manager.find(search)
                        },
                        done = { recipes -> updateRecipes(recipes.toTypedArray()) },
                        error = { updateRecipes(emptyArray()) }
                    )
                }

                return true
            }
        })

        // Set up filter button and menu
        val popFilterMenu = PopupMenu(activity, filterButton)

        run(
            lifecycle = lifecycle,
            callback = Manager::ingredientGetAll,
            done = { ingredients ->

                for (i in ingredients.indices) {
                    val itm = popFilterMenu.menu.add(
                        Menu.NONE,
                        i,
                        Menu.NONE,
                        ingredients[i].name
                    )
                    itm.isCheckable = true
                    itm.isChecked = true
                }
            }
        )

        filterButton.setOnClickListener {
            popFilterMenu.menuInflater.inflate(R.menu.search_filter_menu, popFilterMenu.menu)
            popFilterMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.isChecked) {
                    unwantedIngredients.add(menuItem.itemId)
                } else {
                    unwantedIngredients.remove(menuItem.itemId)
                }
                menuItem.isChecked = !menuItem.isChecked

                run(
                    lifecycle = lifecycle,
                    callback = {
                        val ingredients = Manager.ingredientGetAll()
                        val unwantedNames = ingredients
                            .filterIndexed { i, _ -> unwantedIngredients.contains(i) }
                            .map { i -> i.name }

                        Manager.find(search)
                            .filter { recipe ->
                                recipe.ingredients.all { (name, _) -> !unwantedNames.contains(name) }
                            }
                    },
                    done = { recipes -> updateRecipes(recipes.toTypedArray()) },
                    error = { updateRecipes(emptyArray()) }
                )

                true
            }
            popFilterMenu.show()
        }

        // Initial recipe search
        run(
            lifecycle = lifecycle,
            callback = {
                Manager.find(search)
                    .take(10) // TODO: Could union with `Manager.search`
            },
            done = { recipes -> updateRecipes(recipes.toTypedArray()) }
        )
    }

    /**
     * Updates the displayed recipes in the RecyclerView.
     *
     * @param recipes The array of [RecipeFull] objects to display.
     */
    private fun updateRecipes(recipes: Array<RecipeFull>) {
        val recipeCount: Int = recipes.size

        binding.recipeRecyclerView.adapter = RecipeFullFragmentListAdapter(
            recipes,
            emptyArray(),
            DisplayIngredients.Hide,
        ) { rc -> adapterOnClick(rc) }

        val searchViewModel =
            ViewModelProvider(this)[SearchViewModel::class.java]
        (if (recipeCount == 0) view?.context?.getString(R.string.no_recipes_text) else "")?.let {
            searchViewModel.updateText(
                it
            )
        }
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
     * @param recipe The [RecipeFull] object representing the clicked recipe.
     */
    private fun adapterOnClick(recipe: RecipeFull) {
        val intent = Intent(binding.root.context, PopupRecipeActivity()::class.java)
        intent.putExtra("RECIPE", recipe)
        this.startActivity(intent)

    }
}