package com.example.ecodigify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.ecodigify.databinding.ActivityMainBinding
import com.example.ecodigify.notifications.NotificationScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Manager.init(applicationContext)
        val notificationScheduler = NotificationScheduler(applicationContext)

        run(lifecycle, {
            notificationScheduler.scheduleNotificationForEach(Manager.ingredientGetAll())
        })

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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