package com.chummer5view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_gear.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.mainText
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class VehicleActivity : AppCompatActivity() {
    private lateinit var attribName: String
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gear)
        setSupportActionBar(toolbar_gear)
        setTitle(R.string.action_vehicle)

        if (MainActivity.showGearChilds == 1) {
            switch1.isChecked = true
        }

        switch1.setOnClickListener{
            // Change the switch button checked state on button click
            if (switch1.isChecked) {
                MainActivity.showGearChilds = 1
            } else {
                MainActivity.showGearChilds = 0
            }
            finish()
            startActivity(getIntent())
        }


        mainText.removeAllViews()        //reset TableView
        val tableLayout by lazy { TableLayout(this) }
        var layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        attribName = ""

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            val file = File(MainActivity.PfadZurDatei)
            val fileExists = file.exists()
            if (fileExists) {
                var fertigkeitenHash = HashMap<String, String>()
                val fertigkeitenArray = getElementValuesByAttributeName(
                    readXml(),"/characters/character")

                // Alle Kategorien auslesen
                fertigkeitenArray.forEach { line ->
                    val TextRow = line.split(";")
                    fertigkeitenHash.put(TextRow[0], line)
                }
                // für jede Kategorie die passenden Einträge laden -> Gruppierung entsteht
                val fertigKeitenListe: MutableList<String> = ArrayList()
                fertigkeitenHash.keys.forEach { line ->
                    fertigkeitenArray.forEach { eintrag ->
                        val TextRow = eintrag.split(";")
                        if (TextRow[0] == line) {
                            fertigKeitenListe.add(eintrag)
                        }
                    }

                }

                //getElementValuesByAttributeName(readXml(),"/characters/character/skills/skill","name","base","total").forEach { line ->
                var skillKategorie = "leer"
                fertigKeitenListe.forEach { line ->
                    val TextRow = line.split(";")
                    Log.d("Ausgabe line", line)

                    if (TextRow[0] != skillKategorie) {
                        val row = TableRow(this)
                        row.layoutParams =
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        val params = TableRow.LayoutParams()
                        params.span = 4
                        params.setMargins(10, 20, 0, 0)
                        //row.addView(newButton, 1, params)
                        //row.layoutParams. = 2
                        skillKategorie = TextRow[0]
                        val textfeld0 = TextView(this)
                        textfeld0.apply {
                            text = TextRow[0]
                        }
                        textfeld0.setTypeface(null, Typeface.BOLD);
                        //Zeilenspalte hinzufügen
                        row.addView(textfeld0, params)

                        val textfeld2 = TextView(this)
                        row.addView(textfeld2, layoutParams)
                        val textfeld3 = TextView(this)
                        row.addView(textfeld3, layoutParams)
                        val textfeld4 = TextView(this)
                        row.addView(textfeld4, layoutParams)

                        tableLayout.addView(row)
                    }

                    val row = TableRow(this)
                    row.layoutParams =
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    val textfeld = TextView(this)
                    textfeld.apply {
                        layoutParams.setMargins(50, 20, 0, 0)
                        layoutParams.span = 1
                        //layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)
                        text = TextRow[1]
                    }
                    //Zeilenspalte hinzufügen
                    row.addView(textfeld, layoutParams)

                    tableLayout.addView(row)
                }
                mainText.addView(tableLayout)
            }
        }
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

    public fun readXml(): Document {
        //getContentResolver().openInputStream(uri)
        val xmlFile = File(MainActivity.PfadZurDatei)
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(xmlFile)

        return doc
    }



    public fun getElementValuesByAttributeName(doc: Document, XmlPath: String): List<String> {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        /**
         *
         * /characters/character/vehicles/vehicle
         * /characters/character/vehicles/vehicle/weapons/weapon
         * /characters/character/vehicles/vehicle/mods/mod
         *
         * /characters/character/vehicles/vehicle/gears/gear
         * /characters/character/vehicles/vehicle/gears/gear/children/gear
         */

        val xpath1 = XmlPath + "/vehicles/vehicle"
        val itemsTypeT1 = xPath.evaluate(xpath1, doc, XPathConstants.NODESET) as NodeList
        val itemList: MutableList<String> = ArrayList()
        for (i in 0..itemsTypeT1.length - 1) {
            if (getXPath(itemsTypeT1.item(i)) == "#document/characters/character/vehicles/vehicle") {
                //Log.d("XPath-1: ", getXPath(itemsTypeT1.item(i)))
                val childNodes2 = itemsTypeT1.item(i).childNodes
                //childNodes2.item(i).nodeName
                //Log.d("Test1: ", itemsTypeT1.item(i).textContent)
                var gearName = ""
                for (a in 0..childNodes2.length -1) {
                    if (childNodes2.item(a).nodeName == "name") {
                        Log.d("Test1: ", childNodes2.item(a).textContent)
//                        Log.d("NodeName: ", childNodes2.item(a).nodeName)
//                        Log.d("XPath: ", getXPath(childNodes2.item(a)))
                        gearName = childNodes2.item(a).textContent
                    }
                    if (childNodes2.item(a).nodeName == "category") {
                        if (childNodes2.item(a).textContent.isNotEmpty()) {
                            gearName = childNodes2.item(a).textContent + ";" + gearName
                        } else {
                            gearName = "Sonstiges;" + gearName
                        }
                    }
                    if (childNodes2.item(a).nodeName == "mods" && MainActivity.showGearChilds == 1) {
                        if (childNodes2.item(a).textContent.isNotEmpty()) {
                            val childNodes3 = childNodes2.item(a).childNodes     //gear nochmal unter children
                            Log.d("childrenNode?", childNodes3.item(1).nodeName)
                            for (b in 0..childNodes3.length -1) {
                                val childNodes4 = childNodes3.item(b).childNodes
                                for (c in 0..childNodes4.length -1) {
                                    if (childNodes4.item(c).nodeName == "name") {
                                        gearName = gearName + "\n - " + childNodes4.item(c).textContent
                                        Log.d("childrenName?", childNodes4.item(c).textContent)
                                    }
                                    if (childNodes4.item(c).nodeName == "rating") {
                                        if (childNodes4.item(c).textContent.isNotEmpty() && childNodes4.item(c).textContent != "0") {
                                            gearName = gearName + " (Stufe " + childNodes4.item(c).textContent + ")"
                                        }
                                    }

                                    if (childNodes4.item(c).nodeName == "weapons") {
                                        if (childNodes4.item(c).textContent.isNotEmpty()) {
                                            val childNodes5 = childNodes4.item(c).childNodes     //gear nochmal unter children
                                            //Log.d("childrenNodeSubNode?", childNodes5.item(1).nodeName)
                                            for (d in 0..childNodes5.length -1) {
                                                val childNodes6 = childNodes5.item(d).childNodes
                                                for (e in 0..childNodes6.length -1) {
                                                    if (childNodes6.item(e).nodeName == "name") {
                                                        gearName = gearName + "\n    - " + childNodes6.item(e).textContent
                                                        //Log.d("childrenName?", childNodes6.item(e).textContent)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (childNodes2.item(a).nodeName == "gears" && MainActivity.showGearChilds == 1) {
                        if (childNodes2.item(a).textContent.isNotEmpty()) {
                            val childNodes3 = childNodes2.item(a).childNodes     //gear nochmal unter children
                            Log.d("childrenNode?", childNodes3.item(1).nodeName)
                            for (b in 0..childNodes3.length -1) {
                                val childNodes4 = childNodes3.item(b).childNodes
                                for (c in 0..childNodes4.length -1) {
                                    if (childNodes4.item(c).nodeName == "name") {
                                        gearName = gearName + "\n - " + childNodes4.item(c).textContent
                                        Log.d("childrenName?", childNodes4.item(c).textContent)
                                    }
                                    if (childNodes4.item(c).nodeName == "rating") {
                                        if (childNodes4.item(c).textContent.isNotEmpty() && childNodes4.item(c).textContent != "0") {
                                            gearName = gearName + " (Stufe " + childNodes4.item(c).textContent + ")"
                                        }
                                    }

                                    if (childNodes4.item(c).nodeName == "children") {
                                        if (childNodes4.item(c).textContent.isNotEmpty()) {
                                            val childNodes5 = childNodes4.item(c).childNodes     //gear nochmal unter children
                                            Log.d("childrenNodeSubNode?", childNodes5.item(1).nodeName)
                                            for (d in 0..childNodes5.length -1) {
                                                val childNodes6 = childNodes5.item(d).childNodes
                                                for (e in 0..childNodes6.length -1) {
                                                    if (childNodes6.item(e).nodeName == "name") {
                                                        gearName = gearName + "\n    - " + childNodes6.item(e).textContent
                                                        Log.d("childrenName?", childNodes6.item(e).textContent)
                                                    }
                                                    if (childNodes6.item(e).nodeName == "rating") {
                                                        if (childNodes6.item(e).textContent.isNotEmpty() && childNodes6.item(e).textContent != "0") {
                                                            gearName = gearName + " (Stufe " + childNodes6.item(e).textContent + ")"
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    if (childNodes2.item(a).nodeName == "weapons" && MainActivity.showGearChilds == 1) {
                        if (childNodes2.item(a).textContent.isNotEmpty()) {
                            val childNodes3 = childNodes2.item(a).childNodes     //gear nochmal unter children
                            Log.d("childrenNode?", childNodes3.item(1).nodeName)
                            for (b in 0..childNodes3.length -1) {
                                val childNodes4 = childNodes3.item(b).childNodes
                                for (c in 0..childNodes4.length -1) {
                                    if (childNodes4.item(c).nodeName == "name") {
                                        gearName = gearName + "\n - " + childNodes4.item(c).textContent
                                        Log.d("childrenName?", childNodes4.item(c).textContent)
                                    }
                                }
                            }
                        }
                    }
                }

                if (gearName.isNotEmpty()){
                    itemList.add(gearName + ";")
                }
            }
        }

        return ArrayList(itemList)
    }

    private fun getXPath(node: Node): String {
        val parent = node.getParentNode() ?: return node.getNodeName()
        return getXPath(parent) + "/" + node.getNodeName()
    }

    override fun onStop() {
        super.onStop()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("AlleFertigkeitenAnzeigen_SharedPref", MainActivity.alleFertigkeitenAnzeigen)
            putString("PfadZurDatei_SharedPref",MainActivity.PfadZurDatei)
            putInt("korpZustand_SharedPref", MainActivity.korpZustand)
            putInt("geistZustand_SharedPref", MainActivity.geistZustand)
            putInt("aktEDGWert_SharedPref", MainActivity.aktEDGWert)
            putInt("selectedWeapon_SharedPref", MainActivity.selectedWeapon)
            putInt("aktuellerIniWert_SharedPref", MainActivity.aktuellerIniWert)
            putInt("showGearChilds_SharedPref", MainActivity.showGearChilds)
            putString("aktMunitionWertHashString_SharedPref", MainActivity.aktMunitionWert.entries.toString())
            commit()
        }
    }


}