package com.alexaat.randomdice

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.alexaat.randomdice.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (application.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.layoutMainActivity.setBackgroundResource(R.drawable.dice_background_land)
        } else if (application.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.layoutMainActivity.setBackgroundResource(R.drawable.dice_background_port)
        }

        val navController = findNavController(R.id.nav_host)
        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navDrawer, navController)

        navController.addOnDestinationChangedListener { nc, destination, _ ->
            if (destination.id == nc.graph.startDestination) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.layoutMainActivity.setBackgroundResource(R.drawable.dice_background_land)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.layoutMainActivity.setBackgroundResource(R.drawable.dice_background_port)
        }

    }

}