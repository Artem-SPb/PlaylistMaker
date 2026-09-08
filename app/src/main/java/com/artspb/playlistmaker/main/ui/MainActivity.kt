package com.artspb.playlistmaker.main.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.artspb.playlistmaker.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Включаем EdgeToEdge для главного экрана
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        // 2. Делаем отступ от статус-бара, чтобы контент не уехал под системные часы.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        // 3. Настраиваем Navigation Component и BottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
        
        // 4. Показываем BottomNavigationView только на корневых экранах
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isBottomNavigationVisible = destination.id in setOf(
                R.id.searchFragment, R.id.medialibraryFragment, R.id.settingsFragment
            )
            bottomNavigationView.visibility = if (isBottomNavigationVisible) View.VISIBLE else View.GONE
        }
    }
}
