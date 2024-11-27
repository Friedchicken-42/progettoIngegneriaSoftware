package com.example.ecodigify

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.ecodigify.databinding.ActivityMainBinding
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.db.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "main_db"
        ).build()

        testDB()
    }

    fun testDB() {
        GlobalScope.launch {
            val dao = db.ingredientsDao()
            dao.add(
                Ingredient(1, "Carota", LocalDate.now(), 1),
                Ingredient(2, "Mele", LocalDate.now(), 2)
            )

            dao.remove(1)
            dao.remove(2)
        }
    }
}