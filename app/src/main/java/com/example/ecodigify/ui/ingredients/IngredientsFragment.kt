package com.example.ecodigify.ui.ingredients

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**
 * Fragment for displaying and managing the user's ingredients.
 *
 * This fragment shows a list of the user's ingredients, allowing them to
 * view details, add new ingredients, and scan barcodes to quickly add
 * ingredients. It uses a RecyclerView to display the ingredients and handles
 * navigation to a detailed ingredient view when an ingredient is clicked.
 */
class IngredientsFragment : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

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
        val ingredientsViewModel =
            ViewModelProvider(this).get(IngredientsViewModel::class.java)

        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textIngredients
        val barcodeButton: FloatingActionButton = binding.scanActionButton

        ingredientsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Set up camera permission and barcode scanning behaviour
        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                bitmap?.let {
                    processBarcodeImage(it)
                } ?: run {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_image_taken_text),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    takePictureLauncher.launch(null) // Open the camera if permission is granted
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.camera_permission_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        barcodeButton.setOnClickListener {
            if (isCameraPermissionGranted()) {
                takePictureLauncher.launch(null)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { displayIngredients() }

        return root
    }

    /**
     * Checks if the app has permission to use the camera.
     *
     * @return True if the app has camera permission, false otherwise.
     */
    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Processes the captured barcode image to extract ingredient information.
     *
     * @param bitmap The captured barcode image as a Bitmap.
     */
    private fun processBarcodeImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcodeValue = barcodes.first().rawValue
                    barcodeValue?.let { barcode ->
                        run(
                            lifecycle = lifecycle,
                            callback = { Manager.barcode(barcode) },
                            done = { ingredient -> adapterOnClick(ingredient) },
                            error = {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.barcode_process_error_message_text),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_barcode_detected_text),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.barcode_error_message_text)}: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        displayIngredients()
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
     * Handles clicks on ingredient items in the adapter.
     *
     * This method starts the PopupIngredientsActivity with the selected ingredient.
     *
     * @param ingredient The [Ingredient] object representing the clicked ingredient.
     */
    private fun adapterOnClick(ingredient: Ingredient) {
        val intent = Intent(binding.root.context, PopupIngredientsActivity()::class.java)
        intent.putExtra("INGREDIENT", ingredient)
        activityResultLauncher.launch(intent)
    }

    /**
     * Displays the ingredients in the RecyclerView.
     *
     * This method retrieves the ingredients from the database and sets up the
     * adapter for the RecyclerView. It also updates the ViewModel text based
     * on whether there are any ingredients.
     */
    private fun displayIngredients() {
        run(
            lifecycle = lifecycle,
            callback = Manager::ingredientGetAll,
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