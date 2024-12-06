package com.example.ecodigify.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecodigify.Manager
import com.example.ecodigify.R
import com.example.ecodigify.databinding.FragmentSearchBinding
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.run
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
        var unWantedIngredients: MutableList<Int> = mutableListOf()

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

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
                        function = {
                            delay(250) // Debounce
                            Manager.find(newText)
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
            function = Manager::ingredientGetAll,
            done = { ingredients ->

                for (ing in ingredients) {
                    val itm = popFilterMenu.menu.add(
                        Menu.NONE,
                        ing.id.toInt(),
                        Menu.NONE,
                        ing.name
                    ) // Cast might be problematic!
                    itm.isCheckable = true
                    itm.isChecked = true
                }
            }
        )

        filterButton.setOnClickListener {
            popFilterMenu.menuInflater.inflate(R.menu.search_filter_menu, popFilterMenu.menu)
            popFilterMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.isChecked) {
                    unWantedIngredients.add(menuItem.itemId)
                } else {
                    unWantedIngredients.remove(menuItem.itemId)
                }
                menuItem.isChecked = !menuItem.isChecked

                // TODO: get new filtered recipes from the manager

                true
            }
            popFilterMenu.show()
        }

        run(
            lifecycle = lifecycle,
            function = {
                Manager.search("salt")
                    .take(10)
                    .map { recipe ->
                        Manager.inflate(recipe)
                    }
            },
            done = { recipes -> updateRecipes(recipes.toTypedArray()) }
        )
    }

    private fun updateRecipes(recipes: Array<RecipeFull>) {
        val recipeCount: Int = recipes.size

        binding.recipeRecyclerView.adapter = RecipeFullFragmentListAdapter(
            recipes
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