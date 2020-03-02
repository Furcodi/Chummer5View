package com.chummer5view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MagicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skills)
        setSupportActionBar(toolbar)
        setTitle(R.string.action_magic)
        /**
         *
         * /characters/character/vehicles/vehicle
         * /characters/character/vehicles/vehicle/weapons/weapon
         * /characters/character/vehicles/vehicle/mods/mod
         * /characters/character/vehicles/vehicle/gears/gear
         * /characters/character/vehicles/vehicle/gears/gear/children/gear
         */
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //        // Handle action bar item clicks here. The action bar will
        //        // automatically handle clicks on the Home/Up button, so long
        //        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_skills -> {
                val intent = Intent(this, SkillsActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

            R.id.action_start -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

            R.id.action_fight -> {
                val intent = Intent(this, FightActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

            R.id.action_gear -> {
                val intent = Intent(this, GearActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_cyberware -> {
                val intent = Intent(this, CyberwareActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_vehicle -> {
                val intent = Intent(this, VehicleActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_magic -> {
                val intent = Intent(this, MagicActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }


            else -> super.onOptionsItemSelected(item)

        }
    }


}