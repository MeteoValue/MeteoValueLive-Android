package de.jadehs.mvl.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import de.jadehs.mvl.R
import de.jadehs.mvl.ui.onboarding.Launcher
import de.jadehs.mvl.settings.MainSharedPreferences

class OnboardingActivity : AppCompatActivity(),
    Launcher {

    private lateinit var preference: MainSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preference = MainSharedPreferences(this.baseContext)

        if(preference.introDone){
            startMain()
            return
        }

        setContentView(R.layout.activity_onboarding)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun startMain() {
        startActivity(Intent(this,NavHostActivity::class.java))
        finish()
    }
}