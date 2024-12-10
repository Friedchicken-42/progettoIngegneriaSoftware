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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.favouritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.favouritesRecyclerView.addOnItemTouchListener(object :
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
                        requireView().context.getString(R.string.noRecipesText)
                    } else {
                        ""
                    }
                )
            }
        )
    }
}