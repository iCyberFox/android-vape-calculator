package com.bmweast.vapecalc

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bmweast.vapecalc.databinding.ActivityMainBinding
import com.bmweast.vapecalc.ui.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navContainers: List<LinearLayout>
    private lateinit var navIcons: List<ImageView>
    private lateinit var navTexts: List<TextView>

    private val fragments = mutableMapOf<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navContainers = listOf(
            binding.navLiquidContainer,
            binding.navBaseContainer,
            binding.navBoostContainer,
            binding.navShotContainer,
            binding.navCoilContainer
        )
        navIcons = listOf(
            binding.navLiquidIcon,
            binding.navBaseIcon,
            binding.navBoostIcon,
            binding.navShotIcon,
            binding.navCoilIcon
        )
        navTexts = listOf(
            binding.navLiquidText,
            binding.navBaseText,
            binding.navBoostText,
            binding.navShotText,
            binding.navCoilText
        )

        // Default fragment
        if (savedInstanceState == null) {
            updateNavUI(0)
            val initialFragment = LiquidFragment()
            fragments[0] = initialFragment
            loadFragment(initialFragment)
        }

        setupNavListeners()
    }

    private fun setupNavListeners() {
        binding.navLiquidContainer.setOnClickListener {
            updateNavUI(0)
            val fragment = fragments[0] ?: LiquidFragment().also { fragments[0] = it }
            loadFragment(fragment)
        }
        binding.navBaseContainer.setOnClickListener {
            updateNavUI(1)
            val fragment = fragments[1] ?: BaseFragment().also { fragments[1] = it }
            loadFragment(fragment)
        }
        binding.navBoostContainer.setOnClickListener {
            updateNavUI(2)
            val fragment = fragments[2] ?: BoostFragment().also { fragments[2] = it }
            loadFragment(fragment)
        }
        binding.navShotContainer.setOnClickListener {
            updateNavUI(3)
            val fragment = fragments[3] ?: ShotFragment().also { fragments[3] = it }
            loadFragment(fragment)
        }
        binding.navCoilContainer.setOnClickListener {
            updateNavUI(4)
            val fragment = fragments[4] ?: CoilFragment().also { fragments[4] = it }
            loadFragment(fragment)
        }
    }

    private fun updateNavUI(activeIndex: Int) {
        val colorWhite = ContextCompat.getColor(this, android.R.color.white)
        val colorDark = ContextCompat.getColor(this, R.color.text_primary)

        for (i in navContainers.indices) {
            if (i == activeIndex) {
                navContainers[i].setBackgroundResource(R.drawable.nav_item_bg_active)
                navIcons[i].setColorFilter(colorWhite)
                navTexts[i].setTextColor(colorWhite)
            } else {
                navContainers[i].background = null
                navIcons[i].setColorFilter(colorDark)
                navTexts[i].setTextColor(colorDark)
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val tag = fragment.javaClass.simpleName
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        val transaction = supportFragmentManager.beginTransaction()

        // Hide all existing fragments
        supportFragmentManager.fragments.forEach {
            transaction.hide(it)
        }

        if (existingFragment != null) {
            // If already exists, just show it
            transaction.show(existingFragment)
        } else {
            // If doesn't exist, add it
            transaction.add(R.id.fragment_container, fragment, tag)
        }

        transaction.commit()
    }
}
