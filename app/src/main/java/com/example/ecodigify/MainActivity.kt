package com.example.ecodigify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.ecodigify.databinding.ActivityMainBinding
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.notifications.NotificationScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate

/**
 * Main activity of the application.
 *
 * This activity hosts the navigation components and initializes the application's
 * data manager. It sets up the bottom navigation bar and handles navigation
 * between the different fragments of the application.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the application's data manager
        Manager.init(applicationContext)

        run(lifecycle, {
            val notificationScheduler = NotificationScheduler(applicationContext)
            Manager.ingredientGetAll().forEach { ingredient ->
                val hasScheduled = notificationScheduler.setExpireNotificationFor(ingredient)
                if (hasScheduled) {
                    val newIngredient = ingredient.copy(lastNotified = LocalDate.now())
                    Manager.ingredientRemove(ingredient)
                    Manager.ingredientAdd(newIngredient)
                }
            }
        })

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up bottom navigation
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration(
            setOf(R.id.navigation_ingredients, R.id.navigation_search, R.id.navigation_favourites)
        )
        navView.setupWithNavController(navController)
    }
}