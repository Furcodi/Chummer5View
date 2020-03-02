package com.chummer5view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.w3c.dom.Node


class SkillsActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private lateinit var attribName: String
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skills)
        setSupportActionBar(toolbar)
        textMessage = findViewById(R.id.message)

        setTitle(R.string.action_skills)

        val gesamtZustand = (MainActivity.korpZustand / 3) + (MainActivity.geistZustand / 3)
        mainText.removeAllViews()        //reset TableView
        val tableLayout by lazy { TableLayout(this) }
        var layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        //setContentView(R.layout.activity_main)
        attribName = ""
        /**                <limitphysical>7</limitphysical>
        <limitmental>6</limitmental>
        <limitsocial>5</limitsocial>
        <limitastral>6</limitastral>
         **/
        //attribName = getElementValuesByAttributeName(readXml(), "/characters/character","limitphysical", "limitmental", "").first()

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
                    readXml(),
                    "/characters/character/skills/skill",
                    "name",
                    "base",
                    "total"
                )
                // Alle Kategorien auslesen
                fertigkeitenArray.forEach { line ->
                    val TextRow = line.split(";")
                    fertigkeitenHash.put(TextRow[0], line)
                }
                // für jede Kategorie die passenden Einträge laden -> sortierung entsteht
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

                    //val button = Button(this)
                    //button.apply {
                    val TextRow = line.split(";")

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

                    val textfeld2 = TextView(this)
                    textfeld2.apply {
                        layoutParams.setMargins(50, 20, 0, 0)
                        layoutParams.span = 1
                        //layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)
                        if (TextRow.elementAtOrNull(3) != null) {
                            text = TextRow[2] + "(" + TextRow[3] + ")"
                        } else {
                            text = TextRow[2]
                        }
                    }
                    //Zeilenspalte hinzufügen
                    row.addView(textfeld2, layoutParams)

                    if (gesamtZustand > 0) {
                        val textfeld3 = TextView(this)
                        textfeld3.apply {
                            layoutParams.setMargins(50, 20, 0, 0)
                            layoutParams.span = 1

                            //layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)
                            text = "-" + gesamtZustand.toString()
                        }
                        //Zeilenspalte hinzufügen
                        row.addView(textfeld3, layoutParams)

                        var effektiverSkillWert: Int
                        if (TextRow.elementAtOrNull(3) != null) {
                            effektiverSkillWert = TextRow[3].toInt() - gesamtZustand
                        } else {
                            effektiverSkillWert = TextRow[2].toInt() - gesamtZustand
                        }
                        if (effektiverSkillWert < 0) {
                            effektiverSkillWert = 0
                        }
                        val textfeld4 = TextView(this)
                        textfeld4.apply {
                            layoutParams.setMargins(50, 20, 0, 0)
                            layoutParams.span = 1
                            //layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)
                            text = effektiverSkillWert.toString()
                        }
                        textfeld4.setTypeface(null, Typeface.BOLD);
                        //Zeilenspalte hinzufügen
                        row.addView(textfeld4, layoutParams)
                    }

                    //fertige Zeile hinzufügen.
                    if (!((TextRow.elementAtOrNull(3) == null) && TextRow[2] == "0") || MainActivity.alleFertigkeitenAnzeigen) {
                        tableLayout.addView(row)
                    }

                }
                mainText.addView(tableLayout)

                attribName = "Körperliches Limit: " + MainActivity.korpLimit + "\n" +
                        "Geistiges Limit: " + MainActivity.geistLimit + "\n" +
                        "Soziales Limit: " + MainActivity.sozialLimit + "\n" +
                        "Astrales Limit: " + MainActivity.astralLimit + "\n"
                textMessage.setText(attribName)
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



    public fun getElementValuesByAttributeName(doc: Document, XmlPath: String, AttributeName: String, AttributeWert: String = "", AttributeMod: String = ""): List<String> {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()
        var xpath3 : String

        val xpath = "$XmlPath/$AttributeName"
        //Log.d("val", "$xpath")
        val itemsTypeT1 = xPath.evaluate(xpath, doc, XPathConstants.NODESET) as NodeList

        val xpath2 = "$XmlPath/$AttributeWert"
        val itemsTypeT2 = xPath.evaluate(xpath2, doc, XPathConstants.NODESET) as NodeList

        //if (!AttributeMod.isEmpty()) {
            xpath3 = "$XmlPath/$AttributeMod"
        //} else {
        //    xpath3 = "/characters/character/metatype"
        //}
        val itemsTypeT3 = xPath.evaluate(xpath3, doc, XPathConstants.NODESET) as NodeList

        val xpath4 = "$XmlPath/spec"
        val itemsTypeT4 = xPath.evaluate(xpath4, doc, XPathConstants.NODESET) as NodeList

        val xpath5 = "$XmlPath/specializedrating"
        val itemsTypeT5 = xPath.evaluate(xpath5, doc, XPathConstants.NODESET) as NodeList

        val xpath6 = "$XmlPath/skillcategory"
        val itemsTypeT6 = xPath.evaluate(xpath6, doc, XPathConstants.NODESET) as NodeList

        val itemList: MutableList<String> = ArrayList()
        for (i in 0..itemsTypeT1.length - 1) {
            //Log.d("TAG", itemsTypeT3.item(i).textContent)
            //   !AttributeMod.isEmpty() &&
            if (itemsTypeT3.item(i).textContent != itemsTypeT2.item(i).textContent) {
                itemList.add(itemsTypeT6.item(i).textContent + ";" + itemsTypeT1.item(i).textContent + ";" + itemsTypeT2.item(i).textContent + ";" + itemsTypeT3.item(i).textContent)
            } else {
                itemList.add(itemsTypeT6.item(i).textContent + ";" + itemsTypeT1.item(i).textContent + ";" + itemsTypeT2.item(i).textContent)
            }

            if (itemsTypeT4.item(i).textContent.isNotBlank()) {
                itemList.add(itemsTypeT6.item(i).textContent + ";" + " - " + itemsTypeT4.item(i).textContent + ";" + itemsTypeT5.item(i).textContent)
            }
        }

        val xpathkorpL = "/characters/character/limitphysical"
        val itemsTypeTkorpL = xPath.evaluate(xpathkorpL, doc, XPathConstants.NODESET) as NodeList
        MainActivity.korpLimit = itemsTypeTkorpL.item(0).textContent



        val xpathgeistL = "/characters/character/limitmental"
        val itemsTypeTgeistL = xPath.evaluate(xpathgeistL, doc, XPathConstants.NODESET) as NodeList
        MainActivity.geistLimit = itemsTypeTgeistL.item(0).textContent

        val xpathsozialL = "/characters/character/limitsocial"
        val itemsTypeTsozialL = xPath.evaluate(xpathsozialL, doc, XPathConstants.NODESET) as NodeList
        MainActivity.sozialLimit = itemsTypeTsozialL.item(0).textContent

        val xpathastralL = "/characters/character/limitastral"
        val itemsTypeTastralL = xPath.evaluate(xpathastralL, doc, XPathConstants.NODESET) as NodeList
        MainActivity.astralLimit = itemsTypeTastralL.item(0).textContent

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
