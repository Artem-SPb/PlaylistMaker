package com.artspb.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.artspb.playlistmaker.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MedialibraryFragment : Fragment(R.layout.fragment_medialibrary) {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBar.top)
            insets
        }

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        val adapter = MedialibraryViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager.adapter = adapter

        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_favorites)
                1 -> tab.text = getString(R.string.tab_playlists)
            }
        }
        tabLayoutMediator?.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
    }
}
