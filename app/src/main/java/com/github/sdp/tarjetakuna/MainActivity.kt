package com.github.sdp.tarjetakuna

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.sdp.tarjetakuna.databinding.ActivityDrawerBinding
import com.github.sdp.tarjetakuna.utils.SharedPreferencesKeys
import com.github.sdp.tarjetakuna.utils.SharedPreferencesKeys.shared_pref_name
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDrawerBinding
    private val sharedPrefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == SharedPreferencesKeys.user_name || key == SharedPreferencesKeys.user_description) {
            updateHeader()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the view
        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarDrawer.toolbar)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_browser,
                R.id.nav_scanner,
                R.id.nav_webapi
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Display profile fragment when clicking on the profile icon
        val headerView = binding.navView.getHeaderView(0)
        headerView.findViewById<ImageView>(R.id.profileIcon).setOnClickListener {
            changeFragment(R.id.nav_profile)
            binding.drawerLayout.closeDrawer(binding.navView)
        }

        // Update the header when the user changes their name or description
        val sharedPref = getSharedPreferences(shared_pref_name, Context.MODE_PRIVATE)
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPrefListener)

        updateHeader()
    }

    // Change fragment
    fun changeFragment(fragment: Int, args: Bundle? = null) {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_drawer) as NavHostFragment).navController
        navController.navigate(fragment, args)
    }

    /**
     * Update the header of the navigation drawer to display the user's name and description
     */
    fun updateHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val sharedPref = getSharedPreferences(shared_pref_name, Context.MODE_PRIVATE)
        headerView.findViewById<TextView>(R.id.navHeaderNameText).text = sharedPref.getString(
            SharedPreferencesKeys.user_name, getString(R.string.name_entry_hint)
        )
        headerView.findViewById<TextView>(R.id.navHeaderDescriptionText).text =
            sharedPref.getString(
                SharedPreferencesKeys.user_description, getString(R.string.description_entry_hint)
            )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_drawer) as NavHostFragment).navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
