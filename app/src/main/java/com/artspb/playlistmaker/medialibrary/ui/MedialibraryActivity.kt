package com.artspb.playlistmaker.medialibrary.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewpager2.widget.ViewPager2
import com.artspb.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MedialibraryActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Включаем поддержку Edge-to-Edge для красоты (отрисовка под системными барами)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medialibrary)

        // Делаем отступ сверху на высоту статус-бара, чтобы Toolbar не уехал под системные часы
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val adapter = MedialibraryViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Связываем ViewPager с TabLayout с помощью класса-посредника TabLayoutMediator
        // Он сам следит за перелистыванием и выделяет нужную вкладку
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_favorites)
                1 -> tab.text = getString(R.string.tab_playlists)
            }
        }.attach()
    }
}
