package com.chummer5view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import org.w3c.dom.Node
import android.widget.LinearLayout
import android.R.attr.rowHeight
import android.app.Dialog
import android.content.pm.PackageManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import kotlinx.android.synthetic.main.dialog_permission.*
import java.util.Objects.isNull


class FightActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private lateinit var attribName: String
    private lateinit var weaponSpinner: Spinner
    private lateinit var Panzerungswerte: String
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fight)
        setSupportActionBar(toolbar)
        //textMessage = findViewById(R.id.message)
        setTitle(R.string.action_fight)

        textMessage = findViewById(R.id.koerpWertakt)
        textMessage.setText(MainActivity.korpZustand.toString())
        textMessage = findViewById(R.id.geistWertakt)
        textMessage.setText(MainActivity.geistZustand.toString())

        //val gesamtZustand = (MainActivity.korpZustand / 3) + (MainActivity.geistZustand / 3)

        attribName = ""

        /**
         * TODO Buttons für INI einstellen und INI-Phase-Knopf (-10 INI)
         *      Buttons für Munitionszähler und Reload?
         *      Buttons für Zähler Körperlicher und Geistiger Schaden als +/- weil kein Platz
         *      Spinner: https://www.tutorialkart.com/kotlin-android/android-spinner-kotlin-example/
         */

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
                val weaponArray = getElementValuesByAttributeName(readXml(), "/characters/character/weapons/weapon")

                weaponSpinner = findViewById(R.id.weaponSpinner) as Spinner
                val list = ArrayList<String>()
                weaponArray.forEach { line ->
                    val weaponTags = line.split(";")   //name;damage;dk;accuracy;dicepool;mode;rc;ammo
                    list.add(weaponTags[0])
                }
                if (MainActivity.selectedWeapon > list.size - 1) {
                    MainActivity.selectedWeapon = 0
                }
                val dataAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item, list
                )
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                weaponSpinner.setAdapter(dataAdapter)
                weaponSpinner.setSelection(MainActivity.selectedWeapon)

                //attribName = "INI: " + MainActivity.korpLimit + "\n" +
                //        "INI Matrix: " + MainActivity.geistLimit + "\n"
                //textMessage.setText(attribName)


                weaponSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        //Log.d("test",weaponArray[position])
                        MainActivity.selectedWeapon = position
                        val einzelDatenWeapon = weaponArray[position].split(";")
                        //for (i in 1..einzelDatenWeapon.size - 1 ) {           }
                        textMessage = findViewById(R.id.weaponSchadenWert)
                        textMessage.setText(einzelDatenWeapon[1])
                        textMessage = findViewById(R.id.weaponDKWert)
                        textMessage.setText(einzelDatenWeapon[2])
                        textMessage = findViewById(R.id.weaponAccuracyWert)
                        textMessage.setText(einzelDatenWeapon[3])
                        textMessage = findViewById(R.id.weaponDicePoolWert)
                        textMessage.setText(einzelDatenWeapon[4])
                        textMessage = findViewById(R.id.weaponModeWert)
                        textMessage.setText(einzelDatenWeapon[5])
                        textMessage = findViewById(R.id.weaponRcWert)
                        textMessage.setText(einzelDatenWeapon[6])
                        textMessage = findViewById(R.id.weaponAmmoWert)
                        textMessage.setText(einzelDatenWeapon[7])
                        textMessage = findViewById(R.id.weaponDistWert)
                        textMessage.setText(einzelDatenWeapon[8])
                        refreshCalculatedValues()
                    }

                }

            }
        }


    }


    fun onClickBtnkoerpWertminus(view: View) {
        view.getId()
        if ((MainActivity.korpZustand -1 >= 0)) {
            MainActivity.korpZustand = MainActivity.korpZustand - 1
        }
        refreshCalculatedValues()
    }
    fun onClickBtnkoerpWertplus(view: View) {
        view.getId()
        if (!(MainActivity.korpZustand +1 > MainActivity.maxKorpZustand)) {
            MainActivity.korpZustand = MainActivity.korpZustand + 1
        }
        refreshCalculatedValues()
    }

    fun onClickBtnGeistWertMinus(view: View) {
        view.getId()
        if ((MainActivity.geistZustand -1 >= 0)) {
            MainActivity.geistZustand = MainActivity.geistZustand - 1
        }
        refreshCalculatedValues()
    }
    fun onClickBtnGeistWertPlus(view: View) {
        view.getId()
        if (!(MainActivity.geistZustand +1 > MainActivity.maxGeistZustand)) {
            MainActivity.geistZustand = MainActivity.geistZustand + 1
        }
        refreshCalculatedValues()
    }


    fun onClickBtnIniWertPlusZehn (view: View) {
        view.getId()
        MainActivity.aktuellerIniWert = MainActivity.aktuellerIniWert + 10
        refreshCalculatedValues()
    }
    fun onClickBtnIniWertPlus (view: View) {
        view.getId()
        MainActivity.aktuellerIniWert = MainActivity.aktuellerIniWert + 1
        refreshCalculatedValues()
    }
    fun onClickBtnIniWertMinusZehn (view: View) {
        view.getId()
        if (MainActivity.aktuellerIniWert -10 > 0) {
            MainActivity.aktuellerIniWert = MainActivity.aktuellerIniWert - 10
        } else {
            MainActivity.aktuellerIniWert = 0
        }
        refreshCalculatedValues()
    }
    fun onClickBtnIniWertMinus (view: View) {
        view.getId()
        if (MainActivity.aktuellerIniWert -1 > 0) {
            MainActivity.aktuellerIniWert = MainActivity.aktuellerIniWert - 1
        } else {
            MainActivity.aktuellerIniWert = 0
        }
        refreshCalculatedValues()
    }


    fun onClickBtnIniStdCalc (view: View) {
        view.getId()
        val iniEinzelTeile = MainActivity.iniStdWert.split ("+")
        if (iniEinzelTeile[0].trim().isDigitsOnly()) {
            val aktini = iniEinzelTeile[0].trim().toInt()
            Log.d("Ini Basis", aktini.toString())
            val wuerfelAnzahl = iniEinzelTeile[1].split("W")
            if (wuerfelAnzahl[0].trim().isDigitsOnly()) {
                Log.d("Ini Wuerfel", wuerfelAnzahl[0].toString())
                var wuerfelErgebnis = 0
                for (w in 1..wuerfelAnzahl[0].trim().toInt()) {
                    val randomInteger = (1..6).shuffled().first()
                    wuerfelErgebnis = wuerfelErgebnis + randomInteger
                    Log.d("Ini Wuerfel1", wuerfelErgebnis.toString())
                }
                MainActivity.aktuellerIniWert = aktini + wuerfelErgebnis
            } else {
                MainActivity.aktuellerIniWert = 0
            }
        } else {
            MainActivity.aktuellerIniWert = 0
        }
        refreshCalculatedValues()

    }

    fun onClickBtnSchuss (view: View) {
        view.getId()
        val aktweap = MainActivity.selectedWeapon
        val aktWeapMuni = MainActivity.aktMunitionWert[aktweap]!!.toInt()
        if (aktWeapMuni -1 >= 0 ) {
            MainActivity.aktMunitionWert[aktweap] = aktWeapMuni -1
        }
        refreshCalculatedValues()
    }

    fun onClickBtnReload (view: View) {
        view.getId()
        val aktweap = MainActivity.selectedWeapon
        MainActivity.aktMunitionWert[aktweap] = MainActivity.maxMagazinKapa[aktweap]!!
        refreshCalculatedValues()
    }

    fun onClickBtnPanzerung (view: View) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_permission)
        Panzerungswerte = "Werte der Panzerung, Feuer, Elektro, etc. Widerstände?"
        dialog.textView.setText(Panzerungswerte)
        dialog.show()
    }


    fun refreshCalculatedValues () {
        textMessage = findViewById(R.id.koerpWertakt)
        textMessage.setText(MainActivity.korpZustand.toString())
        textMessage = findViewById(R.id.geistWertakt)
        textMessage.setText(MainActivity.geistZustand.toString())
        textMessage = findViewById(R.id.iniakt)
        textMessage.setText(MainActivity.aktuellerIniWert.toString())
        val aktweap = MainActivity.selectedWeapon
        textMessage = findViewById(R.id.aktAmmoWert)
        if (MainActivity.aktMunitionWert.containsKey(aktweap)) {
            textMessage.setText(MainActivity.aktMunitionWert[aktweap].toString())
        } else {
            textMessage.setText("0")
        }


        val gesamtZustand = (MainActivity.korpZustand / 3) + (MainActivity.geistZustand / 3)
        if (gesamtZustand > 0) {
            textMessage = findViewById(R.id.weaponDicePoolWert)
            val dicePool = textMessage.text
            val waffenTagMultiPool = dicePool.split("(")
            val aktWaffenPool: Int
            var returntext: String = ""
            if (waffenTagMultiPool.size > 1) {
                aktWaffenPool = waffenTagMultiPool[1].dropLast(1).toInt() - gesamtZustand
                returntext = "(-" + gesamtZustand.toString() + ") " + aktWaffenPool.toString()
            } else {
                if (waffenTagMultiPool[0].isDigitsOnly()) {
                    aktWaffenPool = waffenTagMultiPool[0].toInt() - gesamtZustand
                    returntext = "(-" + gesamtZustand.toString() + ") " + aktWaffenPool.toString()
                }
            }
            textMessage = findViewById(R.id.weaponDicePoolMod)
            textMessage.setText(returntext)

        } else {
            textMessage = findViewById(R.id.weaponDicePoolMod)
            textMessage.setText(" ")
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

        //name;damage;dk;accuracy;dicepool;mode;ammo;rc
        val xpath = "$XmlPath/name"
        val itemsTypeT1 = xPath.evaluate(xpath, doc, XPathConstants.NODESET) as NodeList
        val xpath2 = "$XmlPath/damage"
        val itemsTypeT2 = xPath.evaluate(xpath2, doc, XPathConstants.NODESET) as NodeList
        val xpath3 = "$XmlPath/ap"
        val itemsTypeT3 = xPath.evaluate(xpath3, doc, XPathConstants.NODESET) as NodeList
        val xpath4 = "$XmlPath/accuracy"
        val itemsTypeT4 = xPath.evaluate(xpath4, doc, XPathConstants.NODESET) as NodeList
        val xpath5 = "$XmlPath/dicepool"
        val itemsTypeT5 = xPath.evaluate(xpath5, doc, XPathConstants.NODESET) as NodeList
        val xpath6 = "$XmlPath/mode"
        val itemsTypeT6 = xPath.evaluate(xpath6, doc, XPathConstants.NODESET) as NodeList
        val xpath7 = "$XmlPath/rc"
        val itemsTypeT7 = xPath.evaluate(xpath7, doc, XPathConstants.NODESET) as NodeList
        val xpath8 = "$XmlPath/ammo"
        val itemsTypeT8 = xPath.evaluate(xpath8, doc, XPathConstants.NODESET) as NodeList
        val xpath9 = "$XmlPath/ranges"
        val itemsTypeT9 = xPath.evaluate(xpath9, doc, XPathConstants.NODESET) as NodeList


        val itemList: MutableList<String> = ArrayList()
        for (i in 0..itemsTypeT1.length - 1) {   //jede Waffe mit Namen durchgehen
            val childNodes2 = itemsTypeT9.item(i).childNodes
            var reichweiteWerteListe = "("
            for (a in 0..childNodes2.length -1) {
                if (childNodes2.item(a).nodeName == "short") {
                    reichweiteWerteListe =  reichweiteWerteListe + childNodes2.item(a).textContent + "/"
                }
                if (childNodes2.item(a).nodeName == "medium") {
                    reichweiteWerteListe =  reichweiteWerteListe + childNodes2.item(a).textContent + "/"
                }
                if (childNodes2.item(a).nodeName == "long") {
                    reichweiteWerteListe =  reichweiteWerteListe + childNodes2.item(a).textContent + "/"
                }
                if (childNodes2.item(a).nodeName == "extreme") {
                    reichweiteWerteListe =  reichweiteWerteListe + childNodes2.item(a).textContent + "/"
                }
                //Log.d("child", childNodes2.item(a).nodeName)
                //Log.d("text", childNodes2.item(a).textContent)
            }
            reichweiteWerteListe =  reichweiteWerteListe.trimEnd('/') + ")"

                itemList.add(itemsTypeT1.item(i).textContent + ";" + itemsTypeT2.item(i).textContent + ";" + itemsTypeT3.item(i).textContent + ";" +
                        itemsTypeT4.item(i).textContent + ";" + itemsTypeT5.item(i).textContent + ";" + itemsTypeT6.item(i).textContent + ";" +
                        itemsTypeT7.item(i).textContent + ";" + itemsTypeT8.item(i).textContent + ";" + reichweiteWerteListe)

            val re = Regex("[^0-9 ]")    //alle nicht digits entfernen
            val magKapaOnlyDigit = re.replace(itemsTypeT8.item(i).textContent, "")
            MainActivity.maxMagazinKapa[i] = magKapaOnlyDigit.toInt()

        }

        val xpathInistd = "/characters/character/init"
        val inistdxmlread = xPath.evaluate(xpathInistd, doc, XPathConstants.NODESET) as NodeList
        MainActivity.iniStdWert = inistdxmlread.item(0).textContent
        textMessage = findViewById(R.id.iniWertStd)
        textMessage.setText(inistdxmlread.item(0).textContent)

        val xpathIniMatrix = "/characters/character/matrixarinit"
        val iniMatrixxmlread = xPath.evaluate(xpathIniMatrix, doc, XPathConstants.NODESET) as NodeList
        textMessage = findViewById(R.id.iniWertMatrix)
        textMessage.setText(iniMatrixxmlread.item(0).textContent)

        val xpathIniRigger = "/characters/character/riggerinit"
        val iniRiggerxmlread = xPath.evaluate(xpathIniRigger, doc, XPathConstants.NODESET) as NodeList
        textMessage = findViewById(R.id.iniWertRigger)
        textMessage.setText(iniRiggerxmlread.item(0).textContent)

        val xpathIniAstral = "/characters/character/astralinit"
        val iniAstralxmlread = xPath.evaluate(xpathIniAstral, doc, XPathConstants.NODESET) as NodeList
        if (iniAstralxmlread.item(0) != null) {
            textMessage = findViewById(R.id.iniWertAstral)
            textMessage.setText(iniAstralxmlread.item(0).textContent)
        }

        val xpathpanzerWert = "/characters/character/armor"
        val panzerWertxmlread = xPath.evaluate(xpathpanzerWert, doc, XPathConstants.NODESET) as NodeList
        if (panzerWertxmlread.item(0) != null) {
            textMessage = findViewById(R.id.panzerWert)
            textMessage.setText(panzerWertxmlread.item(0).textContent)
        }

        val xpathkoerpmod = "/characters/character/limitmodifiersphys/limitmodifier/name"
        val itemsTypeTkoerpmod = xPath.evaluate(xpathkoerpmod, doc, XPathConstants.NODESET) as NodeList
        var koerpLimitMod = ""
        for (i in 0..itemsTypeTkoerpmod.length - 1) {
            Log.d("Test Limit", itemsTypeTkoerpmod.item(i).textContent )
            koerpLimitMod = koerpLimitMod + "- " + itemsTypeTkoerpmod.item(i).textContent + "\n"
        }
        textMessage = findViewById(R.id.limitModiKoerpWert)
        textMessage.setText(koerpLimitMod.trimEnd())


        val xpathgeistmod = "/characters/character/limitmodifiersment/limitmodifier/name"
        val itemsTypeTgeistmod = xPath.evaluate(xpathgeistmod, doc, XPathConstants.NODESET) as NodeList
        var geistLimitMod = ""
        for (i in 0..itemsTypeTgeistmod.length - 1) {
            Log.d("Test Limit", itemsTypeTgeistmod.item(i).textContent )
            geistLimitMod = geistLimitMod + "- " + itemsTypeTgeistmod.item(i).textContent + "\n"
        }
        textMessage = findViewById(R.id.limitModiGeistWert)
        textMessage.setText(geistLimitMod.trimEnd())

        val xpathsozialmod = "/characters/character/limitmodifierssoc/limitmodifier/name"
        val itemsTypeTsozialmod = xPath.evaluate(xpathsozialmod, doc, XPathConstants.NODESET) as NodeList
        var sozialLimitMod = ""
        for (i in 0..itemsTypeTsozialmod.length - 1) {
            Log.d("Test Limit", itemsTypeTsozialmod.item(i).textContent )
            sozialLimitMod = sozialLimitMod + "- " + itemsTypeTsozialmod.item(i).textContent + "\n"
        }
        textMessage = findViewById(R.id.limitModiSozialWert)
        textMessage.setText(sozialLimitMod.trimEnd())


        return ArrayList(itemList)
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
