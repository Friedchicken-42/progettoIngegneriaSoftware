package com.example.ecodigify.ui.ingredients

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecodigify.Manager
import com.example.ecodigify.R
import com.example.ecodigify.databinding.FragmentIngredientsBinding
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.run
import com.example.ecodigify.ui.adapters.IngredientFragmentListAdapter
import com.example.ecodigify.ui.popup.PopupIngredientsActivity

class IngredientsFragment : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

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

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { displayIngredients() }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        displayIngredients()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adapterOnClick(ingredient: Ingredient) {
        val intent = Intent(binding.root.context, PopupIngredientsActivity()::class.java)
        intent.putExtra("INGREDIENT", ingredient)
        activityResultLauncher.launch(intent)
    }

    private fun displayIngredients() {
        run(
            lifecycle = lifecycle,
            function = Manager::ingredientGetAll,
            done = { ingredients ->
                var ingredients = ingredients.toTypedArray()
                val ingredientCount = ingredients.size

                binding.ingredientsRecyclerView.adapter = IngredientFragmentListAdapter(ingredients,
                    { ing -> adapterOnClick(ing) }) // lambda that opens the popup

                val ingredientsViewModel =
                    ViewModelProvider(this).get(IngredientsViewModel::class.java)

                ingredientsViewModel.updateText(
                    if (ingredientCount == 0) requireView().context.getString(
                        R.string.noIngredientsText
                    ) else ""
                )
            }
        )
    }
}