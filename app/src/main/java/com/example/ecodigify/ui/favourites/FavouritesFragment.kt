package com.example.ecodigify.ui.favourites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecodigify.R
import com.example.ecodigify.databinding.FragmentFavouritesBinding
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.ui.adapters.IngredientFragmentListAdapter
import com.example.ecodigify.ui.adapters.RecipeFullFragmentListAdapter
import com.example.ecodigify.ui.ingredients.IngredientsViewModel
import com.example.ecodigify.ui.popup.PopupIngredientsActivity
import com.example.ecodigify.ui.popup.PopupRecipeActivity
import java.time.LocalDate

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val favouritesViewModel =
            ViewModelProvider(this).get(FavouritesViewModel::class.java)

        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFavourites
        favouritesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.favouritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // TODO: ask to manager for saved recipes
        val recipeCount: Int = 1
        binding.favouritesRecyclerView.adapter = RecipeFullFragmentListAdapter(arrayOf(
            RecipeFull(
                1,
                "recp1",
                Uri.parse("https://m.media-amazon.com/images/I/7143j3vnKbL._AC_SX679_.jpg"),
                instructions = "do something",
                ingredients = listOf(Pair("aaa", "aaa"), Pair("bbb", "bbb"), Pair("ccc", "ccc")),
                source = Uri.parse("https://www.amazon.it//dp/B0DJ13CDR7")),
            RecipeFull(
                1,
                "recp1",
                Uri.parse("https://m.media-amazon.com/images/I/7143j3vnKbL._AC_SX679_.jpg"),
                instructions = "do something",
                ingredients = listOf(Pair("aaa", "aaa"), Pair("bbb", "bbb"), Pair("ccc", "ccc")),
                source = Uri.parse("https://www.amazon.it//dp/B0DJ13CDR7")),
            RecipeFull(
                1,
                "recp1",
                Uri.parse("https://m.media-amazon.com/images/I/7143j3vnKbL._AC_SX679_.jpg"),
                instructions = "do something",
                ingredients = listOf(Pair("aaa", "aaa"), Pair("bbb", "bbb"), Pair("ccc", "ccc")),
                source = Uri.parse("https://www.amazon.it//dp/B0DJ13CDR7")),
            RecipeFull(
                1,
                "recp1",
                Uri.parse("https://m.media-amazon.com/images/I/7143j3vnKbL._AC_SX679_.jpg"),
                instructions = "do something",
                ingredients = listOf(Pair("aaa", "aaa"), Pair("bbb", "bbb"), Pair("ccc", "ccc")),
                source = Uri.parse("https://www.amazon.it//dp/B0DJ13CDR7")),
            RecipeFull(
                1,
                "recp1",
                Uri.parse("https://m.media-amazon.com/images/I/7143j3vnKbL._AC_SX679_.jpg"),
                instructions = "do something",
                ingredients = listOf(Pair("aaa", "aaa"), Pair("bbb", "bbb"), Pair("ccc", "ccc")),
                source = Uri.parse("https://www.amazon.it//dp/B0DJ13CDR7")),
        ),
            { ing -> adapterOnClick(ing) })
        val favouritesViewModel =
            ViewModelProvider(this).get(FavouritesViewModel::class.java)
        favouritesViewModel.updateText( if(recipeCount == 0) view.context.getString(R.string.noRecipesText) else "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adapterOnClick(recipeFull: RecipeFull) {
        val intent = Intent(binding.root.context, PopupRecipeActivity()::class.java)
        intent.putExtra("RECIPE", recipeFull)
        this.startActivity(intent)
    }
}