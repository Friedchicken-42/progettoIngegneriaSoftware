package com.example.ecodigify.ui.ingredients

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecodigify.databinding.FragmentIngredientsBinding
import com.example.ecodigify.ui.adapters.IngredientFragmentListAdapter
import com.example.ecodigify.ui.popup.PopupIngredientsActivity
import com.example.ecodigify.dataclass.Ingredient
import java.time.LocalDate
import com.example.ecodigify.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.core.content.ContextCompat

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
        val barcodeButton: FloatingActionButton = binding.scanActionButton

        ingredientsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        var takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                bitmap?.let {
                    processBarcodeImage(it)
                } ?: run {
                    Toast.makeText(requireContext(), "No image captured", Toast.LENGTH_SHORT).show()
                }
            }
        var requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    takePictureLauncher.launch(null) // Open the camera if permission is granted
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
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

        return root
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun processBarcodeImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcodeValue = barcodes.first().rawValue
                    Toast.makeText(requireContext(), "Barcode detected: $barcodeValue", Toast.LENGTH_SHORT).show()
                    // TODO: call manger to get the ingredient from the API and open the ingredient modification page
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_barcode_detected_text), Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "${getString(R.string.barcode_error_message_text)}: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // TODO: add actual ingredients
        val ingredientCount: Int = 1
        binding.ingredientsRecyclerView.adapter = IngredientFragmentListAdapter(arrayOf(
            Ingredient(1, "Ing1", LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), arrayOf("aaa", "bbb", "ccc").toList(), "2"),
            Ingredient(2, "Ing2", LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), emptyList(),  "2"),
            Ingredient(3, "Ing3", LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), emptyList(), "2"),
            Ingredient(4, "Ing1", LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), emptyList(), "2"),
            Ingredient(5, "Ing2", LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), emptyList(), "2"),
            Ingredient(6, "Ing3", LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), emptyList(), "2"),
            Ingredient(7, "Ing1", LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), emptyList(), "2"),
            Ingredient(8, "Ing2", LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), emptyList(), "2"),
            Ingredient(9, "Ing3", LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), emptyList(), "2"),
            Ingredient(10, "Ing1", LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), emptyList(), "2"),
            Ingredient(11, "Ing2", LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), emptyList(), "2"),
            Ingredient(12, "Ing3", LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), emptyList(), "2"),
            Ingredient(13, "Ing1", LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), emptyList(), "2"),
            Ingredient(14, "Ing2", LocalDate.now().minusDays(4), LocalDate.now().plusDays(2), emptyList(), "2"),
            Ingredient(15, "Ing3", LocalDate.now().minusDays(3), LocalDate.now().plusDays(5), emptyList(), "2")),
            { ing -> adapterOnClick(ing) }) // lambda that opens the popup
        val ingredientsViewModel =
            ViewModelProvider(this).get(IngredientsViewModel::class.java)
        ingredientsViewModel.updateText( if(ingredientCount == 0) view.context.getString(R.string.noIngredientsText) else "")

        view.context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adapterOnClick(ingredient: Ingredient) {
        val intent = Intent(binding.root.context, PopupIngredientsActivity()::class.java)
        intent.putExtra("INGREDIENT", ingredient)
        this.startActivity(intent)
    }
}