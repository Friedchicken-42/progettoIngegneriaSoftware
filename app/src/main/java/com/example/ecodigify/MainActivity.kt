package com.example.ecodigify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.ecodigify.databinding.ActivityMainBinding
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.db.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import kotlin.arrayOf

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "Database").build()
        Manager.init(db)

        // Ingredient for testing
        run(
            lifecycle = lifecycle,
            function = {
                Manager.ingredientAdd(
                    Ingredient(
                        1,
                        "Ing1",
                        LocalDate.now().minusDays(5),
                        LocalDate.now().plusDays(1),
                        arrayOf("aaa", "bbb", "ccc").toList(),
                        "2"
                    )
                )
            },
            done = {},
        )



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_ingredients, R.id.navigation_search, R.id.navigation_favourites
            )
        )
        navView.setupWithNavController(navController)
    }
}