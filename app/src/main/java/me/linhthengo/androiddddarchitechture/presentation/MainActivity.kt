package me.linhthengo.androiddddarchitechture.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.UI.UserProfileActivity
import me.linhthengo.androiddddarchitechture.core.platform.BaseActivity



class MainActivity : BaseActivity() {
    override fun layoutId(): Int = R.layout.activity_main

    private val PERMISSION_REQUEST_CODE = 9001
    private var mLocationPermissionGranted: Boolean = false;


    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile -> {
                val userProfile = Intent(applicationContext, UserProfileActivity::class.java)
                startActivity(userProfile)
                return true
            }
            else ->{
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Action Bar
        setSupportActionBar(findViewById(R.id.toolbar))
        val navController =  findNavController(R.id.nav_host)
        val topLevelDestinations: MutableSet<Int> = HashSet()
        topLevelDestinations.add(R.id.homeFragment)
        topLevelDestinations.add(R.id.splashFragment)
        topLevelDestinations.add(R.id.signInFragment)
        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    toolbar.visibility = View.GONE
//                    bottomNavigationView.visibility = View.GONE
                }
                R.id.signInFragment -> {
                    toolbar.visibility = View.GONE
                }
                R.id.homeFragment -> {
                    toolbar.visibility = View.GONE
                }
                R.id.signUpFragment -> {
                    toolbar.visibility = View.GONE
                }
            }

        }

        // Location Permission
        if (mLocationPermissionGranted) {
            Toast.makeText(this, "Ready to Map!", Toast.LENGTH_SHORT).show()
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Toast.makeText(this,"Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}
