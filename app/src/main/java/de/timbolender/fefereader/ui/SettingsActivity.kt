package de.timbolender.fefereader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.timbolender.fefereader.R
import de.timbolender.fefereader.ui.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, SettingsFragment())
            .commit()
    }

}
