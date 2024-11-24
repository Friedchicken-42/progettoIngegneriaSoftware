package com.example.ecodigify.ui.ingredients

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecodigify.databinding.FragmentIngredientsBinding
import com.example.ecodigify.IngredientFragmentListAdapter
import com.example.ecodigify.ui.popup.PopupIngredientsActivity
import com.example.ecodigify.dataclass.Ingredient
import java.time.LocalDate
import com.example.ecodigify.R
import kotlinx.serialization.json.Json

class IngredientsFragment : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ingredientsViewModel =
            ViewModelProvider(this).get(IngredientsViewModel::class.java)

        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textIngredients
        ingredientsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // TODO: add actual ingredients
        val ingredientCount: Int = 1
        binding.ingredientsRecyclerView.adapter = IngredientFragmentListAdapter(arrayOf(
            Ingredient("id", "Ing1", arrayOf("aaa", "bbb", "ccc").toList(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), "2"),
            Ingredient("id", "Ing2", emptyList(), LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), "2"),
            Ingredient("id", "Ing3", emptyList(), LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), "2"),
            Ingredient("id", "Ing1", emptyList(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), "2"),
            Ingredient("id", "Ing2", emptyList(), LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), "2"),
            Ingredient("id", "Ing3", emptyList(), LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), "2"),
            Ingredient("id", "Ing1", emptyList(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), "2"),
            Ingredient("id", "Ing2", emptyList(), LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), "2"),
            Ingredient("id", "Ing3", emptyList(), LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), "2"),
            Ingredient("id", "Ing1", emptyList(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), "2"),
            Ingredient("id", "Ing2", emptyList(), LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), "2"),
            Ingredient("id", "Ing3", emptyList(), LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), "2"),
            Ingredient("id", "Ing1", emptyList(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), "2"),
            Ingredient("id", "Ing2", emptyList(), LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), "2"),
            Ingredient("id", "Ing3", emptyList(), LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), "2")),
            { ing -> adapterOnClick(ing) }) // lambda that opens the popup
        val ingredientsViewModel =
            ViewModelProvider(this).get(IngredientsViewModel::class.java)
        ingredientsViewModel.updateText( if(ingredientCount == 0) view.context.getString(R.string.noIngredientsText) else "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adapterOnClick(ingredient: Ingredient) {
        val intent = Intent(binding.root.context, PopupIngredientsActivity()::class.java)
        intent.putExtra("INGREDIENT", ingredient)
        //intent.putExtra("INGREDIENT", ingredient.id)
        this.startActivity(intent)
    }
}