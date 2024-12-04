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
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.dataclass.Recipe
import com.example.ecodigify.run
import com.example.ecodigify.ui.adapters.RecipeFragmentListAdapter
import com.example.ecodigify.ui.popup.PopupRecipeActivity
import java.time.LocalDate


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
                    // TODO: actually update the shown recipes from manager using filtered ingredients
                    updateRecipes(emptyArray())
                }
                return true
            }
        })


        // TODO: get some ingredients from the manager?
        val ingredients: Array<Ingredient> = arrayOf(
            Ingredient(1, "ing XD", LocalDate.now(), LocalDate.now(), emptyList(), "4"),
            Ingredient(2, "ing D:", LocalDate.now(), LocalDate.now(), emptyList(), "5"),
            Ingredient(3, "ing :D", LocalDate.now(), LocalDate.now(), emptyList(), "6"),
        )

        val popFilterMenu = PopupMenu(activity, filterButton)

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
            function = { Manager.search("salt") },
            done = { recipes -> updateRecipes(recipes.toTypedArray()) }
        )
    }

    private fun updateRecipes(recipes: Array<Recipe>) {
        val recipeCount: Int = recipes.size

        binding.recipeRecyclerView.adapter = RecipeFragmentListAdapter(
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

    private fun adapterOnClick(recipe: Recipe) {
        val intent = Intent(binding.root.context, PopupRecipeActivity()::class.java)

        run(
            lifecycle = lifecycle,
            function = { Manager.inflate(recipe) },
            done = { recipe ->
                intent.putExtra("RECIPE", recipe)
                this.startActivity(intent)
            }
        )
    }
}