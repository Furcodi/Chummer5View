package com.chummer5view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    companion object {
        var korpLimit = ""
        var geistLimit = ""
        var sozialLimit = ""
        var astralLimit = ""
        var startUp = true
        var alleFertigkeitenAnzeigen = false
        //var abstract PfadZurDatei: Uri
        var PfadZurDatei = "/storage/emulated/0/Download/Schakal.xml"
        var korpZustand = 0
        var maxKorpZustand = 0
        var geistZustand = 0
        var maxGeistZustand = 0
        var aktEDGWert = 0
        var selectedWeapon = 0
        var aktuellerIniWert = 0
        var iniStdWert = ""
        var aktMunitionWert: HashMap<Int, Int> = hashMapOf()
        var maxMagazinKapa: HashMap<Int, Int> = hashMapOf()
        var showGearChilds = 0
    }

    private lateinit var textMessage: TextView
    private var AttribArray: ArrayList<String> = arrayListOf()
    private lateinit var CharName: String
    private lateinit var VorteilListe: String
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101
    //private lateinit var attribName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //load settings on startup
        if (startUp) {
            val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
            alleFertigkeitenAnzeigen = sharedPref.getBoolean("AlleFertigkeitenAnzeigen_SharedPref", false)
            PfadZurDatei = sharedPref.getString("PfadZurDatei_SharedPref", "<leer>")!!
            korpZustand = sharedPref.getInt("korpZustand_SharedPref", 0)
            geistZustand = sharedPref.getInt("geistZustand_SharedPref", 0)
            aktEDGWert = sharedPref.getInt("aktEDGWert_SharedPref", 0)
            selectedWeapon = sharedPref.getInt("selectedWeapon_SharedPref", 0)
            aktuellerIniWert = sharedPref.getInt("aktuellerIniWert_SharedPref", 0)
            showGearChilds = sharedPref.getInt("showGearChilds_SharedPref", 0)
            val aktMunitionWertHashString =
                sharedPref.getString("aktMunitionWertHashString_SharedPref", "").toString()  //[1=14, 2=12]
            // Wir basteln uns einen Hash zurück
            var mun1 = aktMunitionWertHashString.trim('[', ']')
            mun1 = mun1.replace("\\s".toRegex(), "")
            val munArrayArray = mun1.split(",")
            munArrayArray.forEach { munPair ->
                val munKeysAndValues = munPair.split("=")
                if (munKeysAndValues[0].isNotEmpty()) {
                    aktMunitionWert[munKeysAndValues[0].toInt()] = munKeysAndValues[1].toInt()
                }
            }
            startUp = false
        }


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_permission)
            dialog.show()

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {

            val file = File(PfadZurDatei)
            val fileExists = file.exists()
            if (fileExists) {
                getElementValuesByAttributeName(
                    readXml(),
                    "/characters/character/attributes/attribute",
                    "name",
                    "base",
                    "total"
                )
            } else {
                CharName = "not loaded"
                VorteilListe = "-"
            }
            //getElementValuesByAttributeName(readXml(), "/characters/character/attributes/attribute","name", "base", "total")
            setTitle(CharName)
            //attribName = ""
            AttribArray.forEach { line ->
                val DatenArray = line.split(";")
                when (DatenArray[0]) {
                    "KON" -> {
                        textMessage = findViewById(R.id.KON_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                            // Zustandsmonitor Kästchenberechnung
                            val voncb = 8 + (DatenArray[2].toInt() / 2)
                            maxKorpZustand = voncb
                            for (i in (voncb + 1)..18) {
                                val checkBoxID = "korpCheckBox$i"
                                val resID = resources.getIdentifier(checkBoxID, "id", packageName)
                                val checkBoxName = findViewById(resID) as CheckBox
                                checkBoxName.setEnabled(false)
                            }
                        } else {
                            textMessage.setText(DatenArray[1])
                            val voncb = 8 + (DatenArray[1].toInt() / 2)
                            maxKorpZustand = voncb
                            for (i in (voncb + 1)..18) {
                                val checkBoxID = "korpCheckBox$i"
                                val resID = resources.getIdentifier(checkBoxID, "id", packageName)
                                val checkBoxName = findViewById(resID) as CheckBox
                                checkBoxName.setEnabled(false)
                            }
                        }
                    }
                    "GES" -> {
                        textMessage = findViewById(R.id.GES_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                        } else {
                            textMessage.setText(DatenArray[1])
                        }
                    }
                    "REA" -> {
                        textMessage = findViewById(R.id.REA_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                        } else {
                            textMessage.setText(DatenArray[1])
                        }
                    }
                    "STR" -> {
                        textMessage = findViewById(R.id.STR_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                        } else {
                            textMessage.setText(DatenArray[1])
                        }
                    }
                    "CHA" -> {
                        textMessage = findViewById(R.id.CHA_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                        } else {
                            textMessage.setText(DatenArray[1])
                        }
                    }
                    "INT" -> {
                        textMessage = findViewById(R.id.INT_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                        } else {
                            textMessage.setText(DatenArray[1])
                        }
                    }
                    "LOG" -> {
                        textMessage = findViewById(R.id.LOG_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                        } else {
                            textMessage.setText(DatenArray[1])
                        }
                    }
                    "WIL" -> {
                        textMessage = findViewById(R.id.WIL_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                            // Zustandsmonitor Kästchenberechnung
                            val voncb = 8 + (DatenArray[2].toInt() / 2)
                            maxGeistZustand = voncb
                            for (i in (voncb + 1)..12) {
                                val checkBoxID = "geistCheckBox$i"
                                val resID = resources.getIdentifier(checkBoxID, "id", packageName)
                                val checkBoxName = findViewById(resID) as CheckBox
                                checkBoxName.setEnabled(false)
                            }
                        } else {
                            textMessage.setText(DatenArray[1])
                            // Zustandsmonitor Kästchenberechnung
                            val voncb = 8 + (DatenArray[1].toInt() / 2)
                            maxGeistZustand = voncb
                            for (i in (voncb + 1)..12) {
                                val checkBoxID = "geistCheckBox$i"
                                val resID = resources.getIdentifier(checkBoxID, "id", packageName)
                                val checkBoxName = findViewById(resID) as CheckBox
                                checkBoxName.setEnabled(false)
                            }
                        }
                    }
                    "EDG" -> {
                        textMessage = findViewById(R.id.EDG_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                            val edgeAktuell = DatenArray[2].toInt() + aktEDGWert
                            //EDGE aktuell anpassen
                            textMessage = findViewById(R.id.txt_akt_EDG)
                            textMessage.setText(edgeAktuell.toString())
                        } else {
                            textMessage.setText(DatenArray[1])
                            val edgeAktuell = DatenArray[1].toInt() + aktEDGWert
                            //EDGE aktuell anpassen
                            textMessage = findViewById(R.id.txt_akt_EDG)
                            textMessage.setText(edgeAktuell.toString())
                        }
                    }
                    "MAG" -> {
                        textMessage = findViewById(R.id.MAG_Wert)
                        if (DatenArray.elementAtOrNull(2) != null) {
                            textMessage.setText(DatenArray[1] + DatenArray[2])
                        } else {
                            textMessage.setText(DatenArray[1])
                        }
                    }

                }
            }
            textMessage = findViewById(R.id.vorteile)
            textMessage.setText(VorteilListe)

            if (korpZustand > 0) {
                checkBoxFillAll(korpZustand, 1, 18, "korp", true)
            }
            if (geistZustand > 0) {
                checkBoxFillAll(geistZustand, 1, 12, "geist", true)
            }


            /**
             *  Checkboxhandling
             *  ToDo checkBoxcmpltpass.setEnabled(false) um überzählige zu deaktivieren
             */
            korpCheckBox1.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(1, 1, 18, "korp", isChecked)
            }
            korpCheckBox2.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(2, 1, 18, "korp", isChecked)
            }
            korpCheckBox3.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(3, 1, 18, "korp", isChecked)
            }
            korpCheckBox4.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(4, 1, 18, "korp", isChecked)
            }
            korpCheckBox5.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(5, 1, 18, "korp", isChecked)
            }
            korpCheckBox6.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(6, 1, 18, "korp", isChecked)
            }
            korpCheckBox7.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(7, 1, 18, "korp", isChecked)
            }
            korpCheckBox8.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(8, 1, 18, "korp", isChecked)
            }
            korpCheckBox9.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(9, 1, 18, "korp", isChecked)
            }
            korpCheckBox10.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(10, 1, 18, "korp", isChecked)
            }
            korpCheckBox11.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(11, 1, 18, "korp", isChecked)
            }
            korpCheckBox12.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(12, 1, 18, "korp", isChecked)
            }
            korpCheckBox13.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(13, 1, 18, "korp", isChecked)
            }
            korpCheckBox14.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(14, 1, 18, "korp", isChecked)
            }
            korpCheckBox15.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(15, 1, 18, "korp", isChecked)
            }
            korpCheckBox16.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(16, 1, 18, "korp", isChecked)
            }
            korpCheckBox17.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(17, 1, 18, "korp", isChecked)
            }
            korpCheckBox18.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(18, 1, 18, "korp", isChecked)
            }

            geistCheckBox1.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(1, 1, 12, "geist", isChecked)
            }
            geistCheckBox2.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(2, 1, 12, "geist", isChecked)
            }
            geistCheckBox3.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(3, 1, 12, "geist", isChecked)
            }
            geistCheckBox4.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(4, 1, 12, "geist", isChecked)
            }
            geistCheckBox5.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(5, 1, 12, "geist", isChecked)
            }
            geistCheckBox6.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(6, 1, 12, "geist", isChecked)
            }
            geistCheckBox7.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(7, 1, 12, "geist", isChecked)
            }
            geistCheckBox8.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(8, 1, 12, "geist", isChecked)
            }
            geistCheckBox9.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(9, 1, 12, "geist", isChecked)
            }
            geistCheckBox10.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(10, 1, 12, "geist", isChecked)
            }
            geistCheckBox11.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(11, 1, 12, "geist", isChecked)
            }
            geistCheckBox12.setOnCheckedChangeListener { _, isChecked ->
                checkBoxFillAll(12, 1, 12, "geist", isChecked)
            }
        }

    }

    fun checkBoxFillAll(checkBoxNumber: Int, checkBoxNumberVon: Int, checkBoxNumberBis: Int, checkBoxPrefix: String, angehakt: Boolean): Boolean {
        if (angehakt) {
            for (i in checkBoxNumberVon..checkBoxNumber) {
                val checkBoxID = checkBoxPrefix + "CheckBox$i"
                val resID = resources.getIdentifier(checkBoxID, "id", packageName)
                val checkBoxName = findViewById(resID) as CheckBox
                checkBoxName.setChecked(true)
            }
            for (i in checkBoxNumber+1..checkBoxNumberBis) {
                val checkBoxID = checkBoxPrefix + "CheckBox$i"
                val resID = resources.getIdentifier(checkBoxID, "id", packageName)
                val checkBoxName = findViewById(resID) as CheckBox
                checkBoxName.setChecked(false)
            }
            if (checkBoxPrefix == "korp") {
                korpZustand = checkBoxNumber
            }
            if (checkBoxPrefix == "geist") {
                geistZustand = checkBoxNumber
            }
            var anzeigeTextZuKoerp = getString(R.string.zustand_koerper_txt)
            if ((korpZustand / 3) > 0) { anzeigeTextZuKoerp = getString(R.string.zustand_koerper_txt) + " (-" + (korpZustand / 3).toString() + ")" }
            var anzeigeTextZuGeist = getString(R.string.zustand_geist_txt)
            if ((geistZustand / 3) > 0) { anzeigeTextZuGeist = getString(R.string.zustand_geist_txt) + " (-" + (geistZustand / 3).toString() + ")" }
            textMessage = findViewById(R.id.zustandKorp); textMessage.setText(anzeigeTextZuKoerp)
            textMessage = findViewById(R.id.zustandGeist); textMessage.setText(anzeigeTextZuGeist)
        } else {
            //korpZustand = 0
            /**for (i in checkBoxNumberVon..(checkBoxNumber -1)) {
                val checkBoxID = checkBoxPrefix + "CheckBox$i"
                val resID = resources.getIdentifier(checkBoxID, "id", packageName)
                val checkBoxName = findViewById(resID) as CheckBox
                //checkBoxName.setChecked(true)
            }
            */
            for (i in checkBoxNumber..checkBoxNumberBis) {
                val checkBoxID = checkBoxPrefix + "CheckBox$i"
                val resID = resources.getIdentifier(checkBoxID, "id", packageName)
                val checkBoxName = findViewById(resID) as CheckBox
                checkBoxName.setChecked(false)
            }
            if (checkBoxPrefix == "korp") {
                korpZustand = checkBoxNumber - 1
            }
            if (checkBoxPrefix == "geist") {
                geistZustand = checkBoxNumber - 1
            }
            var anzeigeTextZuKoerp = getString(R.string.zustand_koerper_txt)
            if ((korpZustand / 3) > 0) { anzeigeTextZuKoerp = getString(R.string.zustand_koerper_txt) + " (-" + (korpZustand / 3).toString() + ")" }
            var anzeigeTextZuGeist = getString(R.string.zustand_geist_txt)
            if ((geistZustand / 3) > 0) { anzeigeTextZuGeist = getString(R.string.zustand_geist_txt) + " (-" + (geistZustand / 3).toString() + ")" }
            textMessage = findViewById(R.id.zustandKorp); textMessage.setText(anzeigeTextZuKoerp)
            textMessage = findViewById(R.id.zustandGeist); textMessage.setText(anzeigeTextZuGeist)
        }
        return true
    }

    fun onClickBtnEdgeMinus(view: View) {
        //EDGE aktuell anpassen
        view.getId()
        textMessage = findViewById(R.id.EDG_Wert)
        val charEdge = textMessage.getText().toString()
        if (!((charEdge.toInt() + aktEDGWert.toInt()) - 1 < 0)) {
            aktEDGWert = aktEDGWert - 1
            val edgeAktuell = charEdge.toInt() + aktEDGWert.toInt()
            textMessage = findViewById(R.id.txt_akt_EDG)
            textMessage.setText(edgeAktuell.toString())
        }
    }

    fun onClickBtnEdgePlus(view: View) {
        //EDGE aktuell anpassen
        view.getId()
        textMessage = findViewById(R.id.EDG_Wert)
        val charEdge = textMessage.getText().toString()
        if (!(aktEDGWert +1 > 0)) {
            aktEDGWert = aktEDGWert + 1
            val edgeAktuell = charEdge.toInt() + aktEDGWert
            textMessage = findViewById(R.id.txt_akt_EDG)
            textMessage.setText(edgeAktuell.toString())
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
        //        // Handle action bar item clicks here. The action bar will
        //        // automatically handle clicks on the Home/Up button, so long
        //        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }


            R.id.action_start -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }


            R.id.action_skills -> {
                val intent = Intent(this, SkillsActivity::class.java)
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



    fun readXml(): Document {
        val xmlFile = File(MainActivity.PfadZurDatei)
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(xmlFile)

        return doc
    }

    fun getElementValuesByAttributeName(doc: Document, XmlPath: String, AttributeName: String, AttributeWert: String = "", AttributeMod: String = ""): Boolean {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()
        var xpath3 : String

        val xpath = "$XmlPath/$AttributeName"
        //Log.d("val", "$xpath")
        val itemsTypeT1 = xPath.evaluate(xpath, doc, XPathConstants.NODESET) as NodeList

        val xpath2 = "$XmlPath/$AttributeWert"
        val itemsTypeT2 = xPath.evaluate(xpath2, doc, XPathConstants.NODESET) as NodeList

        if (!AttributeMod.isEmpty()) {
            xpath3 = "$XmlPath/$AttributeMod"
        } else {
            xpath3 = "/characters/character/metatype"
        }
        val itemsTypeT3 = xPath.evaluate(xpath3, doc, XPathConstants.NODESET) as NodeList

        val itemList: MutableList<String> = ArrayList()
        for (i in 0..itemsTypeT1.length - 1) {
            //Log.d("TAG", itemsTypeT3.item(i).textContent)
            if (!AttributeMod.isEmpty() && itemsTypeT3.item(i).textContent != itemsTypeT2.item(i).textContent) {
                itemList.add(itemsTypeT1.item(i).textContent + ";" + itemsTypeT2.item(i).textContent + ";(" + itemsTypeT3.item(i).textContent + ")")
            } else {
                itemList.add(itemsTypeT1.item(i).textContent + ";" + itemsTypeT2.item(i).textContent)
            }
        }
        AttribArray = ArrayList(itemList)

        /**var xpathMore = "/characters/character/limitphysical"
        var itemsTypeTMore = xPath.evaluate(xpathMore, doc, XPathConstants.NODESET) as NodeList
        MainActivity.globalVar = itemsTypeTMore.item(0).textContent
*/
        var xpathMore = "/characters/character/alias"
        var itemsTypeTMore = xPath.evaluate(xpathMore, doc, XPathConstants.NODESET) as NodeList
        CharName = itemsTypeTMore.item(0).textContent

        //characters/character/<qualities>/<quality><name>Restlichtverstärkung</name>
        xpathMore = "/characters/character/qualities/quality/name"
        itemsTypeTMore = xPath.evaluate(xpathMore, doc, XPathConstants.NODESET) as NodeList
        VorteilListe = ""
        for (i in 0..itemsTypeTMore.length -1 ) {
            VorteilListe = VorteilListe + itemsTypeTMore.item(i).textContent + "\n"
        }
        //return ArrayList(itemList)
        return true
    }


    override fun onStop() {
        super.onStop()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("AlleFertigkeitenAnzeigen_SharedPref", alleFertigkeitenAnzeigen)
            putString("PfadZurDatei_SharedPref",PfadZurDatei)
            putInt("korpZustand_SharedPref", korpZustand)
            putInt("geistZustand_SharedPref", geistZustand)
            putInt("aktEDGWert_SharedPref", aktEDGWert)
            putInt("selectedWeapon_SharedPref", selectedWeapon)
            putInt("aktuellerIniWert_SharedPref", aktuellerIniWert)
            putInt("showGearChilds_SharedPref", showGearChilds)
            putString("aktMunitionWertHashString_SharedPref", MainActivity.aktMunitionWert.entries.toString())
            commit()
        }
    }


}
