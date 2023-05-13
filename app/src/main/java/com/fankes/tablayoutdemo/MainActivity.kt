@file:Suppress("SetTextI18n")

package com.fankes.tablayoutdemo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fankes.tablayoutdemo.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).also { binding ->
            setContentView(binding.root)
            val views = arrayListOf<TextView>()
            for (i in 1..2)
                views.add(TextView(this).apply {
                    text = "Sample Page $i"
                    textSize = 20f
                    gravity = Gravity.CENTER
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                })
            binding.viewPager.adapter = object : PagerAdapter() {

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    return views[position].apply { container.addView(this) }
                }

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(views[position])
                }

                override fun getCount() = views.size

                override fun isViewFromObject(view: View, `object`: Any): Boolean {
                    return view == `object`
                }
            }
            arrayOf(
                binding.tabLayoutSimple1,
                binding.tabLayoutSimple2
            ).forEach {
                it.addOnTabSelectedListener(object : OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        binding.viewPager.currentItem = tab?.position ?: 0
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
            }
            // This listener will not cause problem
            binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                private val tabLayout get() = binding.tabLayoutSimple1

                private var previousScrollState = 0
                private var scrollState = 0

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    val updateText = scrollState != ViewPager.SCROLL_STATE_SETTLING || previousScrollState == ViewPager.SCROLL_STATE_DRAGGING
                    val updateIndicator = !(scrollState == ViewPager.SCROLL_STATE_SETTLING && previousScrollState == ViewPager.SCROLL_STATE_IDLE)
                    tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator)
                }

                override fun onPageSelected(position: Int) {
                    if (tabLayout.selectedTabPosition == position || position >= tabLayout.tabCount) return
                    val updateIndicator = (scrollState == ViewPager.SCROLL_STATE_IDLE
                            || (scrollState == ViewPager.SCROLL_STATE_SETTLING
                            && previousScrollState == ViewPager.SCROLL_STATE_IDLE))
                    tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // If I dont calls this, This will cause animation problem
                    tabLayout.javaClass.getDeclaredMethod("updateViewPagerScrollState", Int::class.javaPrimitiveType)
                        .apply { isAccessible = true }.invoke(tabLayout, state)
                    previousScrollState = scrollState
                    scrollState = state
                }
            })
            // This listener will cause problem
            binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                private val tabLayout get() = binding.tabLayoutSimple2

                private var previousScrollState = 0
                private var scrollState = 0

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    val updateText = scrollState != ViewPager.SCROLL_STATE_SETTLING || previousScrollState == ViewPager.SCROLL_STATE_DRAGGING
                    val updateIndicator = !(scrollState == ViewPager.SCROLL_STATE_SETTLING && previousScrollState == ViewPager.SCROLL_STATE_IDLE)
                    tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator)
                }

                override fun onPageSelected(position: Int) {
                    if (tabLayout.selectedTabPosition == position || position >= tabLayout.tabCount) return
                    val updateIndicator = (scrollState == ViewPager.SCROLL_STATE_IDLE
                            || (scrollState == ViewPager.SCROLL_STATE_SETTLING
                            && previousScrollState == ViewPager.SCROLL_STATE_IDLE))
                    tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // removed updateViewPagerScrollState method
                    previousScrollState = scrollState
                    scrollState = state
                }
            })
        }
    }
}