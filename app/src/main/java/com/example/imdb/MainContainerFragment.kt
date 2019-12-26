package com.example.imdb.series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imdb.R
import com.example.imdb.ViewPagerAdapter
import com.example.imdb.extension.SwipeLockableViewPager
import com.example.imdb.movie.MoviesFragment
import com.example.imdb.profile.ProfileFragment
import com.example.imdb.search.SearchFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_main_container.view.*

class MainContainerFragment : Fragment() {
    private var adapter: ViewPagerAdapter? = null
    private lateinit var viewPager: SwipeLockableViewPager
    lateinit var tabLayout: TabLayout

    companion object {
        fun newInstance(): MainContainerFragment {
            return MainContainerFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.viewPager
        viewPager.setSwipePagingEnabled(false)
        tabLayout = view.tabLayout
        viewPager.offscreenPageLimit = 3
        adapter = fragmentManager?.let { ViewPagerAdapter(it) }

        listOf(MoviesFragment(), TvSeriesFragment(), SearchFragment(), ProfileFragment()).forEach {
            adapter?.addFragment(it, "")
        }

        viewPager.setAdapter(adapter)
        viewPager.setCurrentItem(0, true)
        tabLayout.run {
            setupWithViewPager(this@MainContainerFragment.viewPager)
            getTabAt(0)?.setIcon(R.drawable.selector_tab_item_movie)
            getTabAt(1)?.setIcon(R.drawable.selector_tab_item_serie)
            getTabAt(2)?.setIcon(R.drawable.selector_tab_item_search)
            getTabAt(3)?.setIcon(R.drawable.selector_tab_item_profile)
        }
    }

}
