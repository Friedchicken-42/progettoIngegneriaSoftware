package com.example.ecodigify.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ecodigify.R
import com.example.ecodigify.ui.adapters.RecipeFragmentListAdapter
import com.example.ecodigify.databinding.FragmentSearchBinding
import com.example.ecodigify.dataclass.Recipe
import com.example.ecodigify.ui.popup.PopupRecipeActivity

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
            ViewModelProvider(this).get(SearchViewModel::class.java)

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

        filterButton.setOnClickListener {
            println("Filter button clicked") // TODO: implement
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                this.onQueryTextChange(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    println("Search text changed: $newText") // TODO: implement
                }
                return true
            }
        })

        // TODO: get some recipes from the manager?
        val recipeCount: Int = 0

        binding.recipeRecyclerView.adapter = RecipeFragmentListAdapter(
            emptyArray(), // TODO: add test data
            { rc -> adapterOnClick(rc) })

        val searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)
        searchViewModel.updateText(if(recipeCount == 0) view.context.getString(R.string.noRecipesText) else "")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adapterOnClick(recipe: Recipe) {
        val intent = Intent(binding.root.context, PopupRecipeActivity()::class.java)
        intent.putExtra("RECIPE", recipe)
        this.startActivity(intent)
    }
}