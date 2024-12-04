package com.example.ecodigify.ui.favourites

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
import com.example.ecodigify.databinding.FragmentFavouritesBinding
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.run
import com.example.ecodigify.ui.adapters.RecipeFullFragmentListAdapter
import com.example.ecodigify.ui.popup.PopupRecipeActivity

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

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

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { display() }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.favouritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        display()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adapterOnClick(recipeFull: RecipeFull) {
        val intent = Intent(binding.root.context, PopupRecipeActivity()::class.java)
        intent.putExtra("RECIPE", recipeFull)
        activityResultLauncher.launch(intent)
    }

    private fun display() {
        run(
            lifecycle = lifecycle,
            function = Manager::recipeGetAll,
            done = { recipes ->
                println(recipes)

                binding.favouritesRecyclerView.adapter =
                    RecipeFullFragmentListAdapter(recipes.toTypedArray(),
                        { ing -> adapterOnClick(ing) })
                val favouritesViewModel =
                    ViewModelProvider(this).get(FavouritesViewModel::class.java)
                favouritesViewModel.updateText(
                    if (recipes.isEmpty()) {
                        requireView().context.getString(R.string.noRecipesText)
                    } else {
                        ""
                    }
                )
            }
        )
    }
}