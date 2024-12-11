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


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var searchJob: Job? = null

    // TODO: switch search
    private var search: String = "cake"

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView: SearchView = binding.searchView
        val filterButton: ImageButton = binding.filterButton
        val unwantedIngredients: MutableList<Int> = mutableListOf()

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recipeRecyclerView.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val viewChild = rv.findChildViewUnder(e.x, e.y)
                val ingredientsRecyclerView =
                    viewChild?.findViewById<RecyclerView>(R.id.recipeFullIngredientsRecyclerView)

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
                    callback = { Manager.filter(search, unwantedIngredients) },
                    done = { recipes -> updateRecipes(recipes.toTypedArray()) },
                    error = { updateRecipes(emptyArray()) }
                )

                true
            }
            popFilterMenu.show()
        }

        run(
            lifecycle = lifecycle,
            callback = {
                Manager.find(search)
                    .take(10) // TODO: Could union with `Manager.search`
            },
            done = { recipes -> updateRecipes(recipes.toTypedArray()) }
        )
    }

    private fun updateRecipes(recipes: Array<RecipeFull>) {
        val recipeCount: Int = recipes.size

        binding.recipeRecyclerView.adapter = RecipeFullFragmentListAdapter(
            recipes,
            emptyArray(),
            DisplayIngredients.Hide,
        ) { rc -> adapterOnClick(rc) }

        val searchViewModel =
            ViewModelProvider(this)[SearchViewModel::class.java]
        (if (recipeCount == 0) view?.context?.getString(R.string.noRecipesText) else "")?.let {
            searchViewModel.updateText(
                it
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adapterOnClick(recipe: RecipeFull) {
        val intent = Intent(binding.root.context, PopupRecipeActivity()::class.java)
        intent.putExtra("RECIPE", recipe)
        this.startActivity(intent)

    }
}