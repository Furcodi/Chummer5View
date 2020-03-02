package com.chummer5view


import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import kotlinx.android.synthetic.main.settings.*
import java.io.File
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.documentfile.provider.DocumentFile
import java.io.IOException
import java.security.AccessController.getContext


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val actionbar = supportActionBar
        //set actionbar title
        //setTitle(R.string.action_settings)
        actionbar!!.title = getString(R.string.action_settings)
        val pfadvariable = findViewById<EditText>(R.id.DateiName)
        pfadvariable.setText(MainActivity.PfadZurDatei)
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

/**        val layout2 = findViewById<View>(R.id.settings_layout) as LinearLayout
        //layout = (LinearLayout) findViewById(R.id.mainText);
        val shotButton = Button(this)
        // setting layout_width and layout_height using layout parameters
        shotButton.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        shotButton.text = "Dynamic Button"
        // add Button to LinearLayout
        shotButton.setBackgroundResource(R.drawable.shoot_button)

        //add button to the layout
        layout2.addView(shotButton)
**/
        val AlleFertigkeitenAnzeigencb = findViewById(R.id.cbAlleFertigkeitenAnzeigen) as CheckBox
        if (MainActivity.alleFertigkeitenAnzeigen) {
            AlleFertigkeitenAnzeigencb.setChecked(true)
        }

        AlleFertigkeitenAnzeigencb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                MainActivity.alleFertigkeitenAnzeigen = true
            } else {
                MainActivity.alleFertigkeitenAnzeigen = false
            }
        }

        BtnLoadDat.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }


        BtnLoadDatTest.setOnClickListener {
            val intent = Intent(this, SelectFileActivity::class.java)
            startActivity(intent)
            //finish()
        }


    }

    /**

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            return cursor!!.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor!!.close()
            }
        }
    }
*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file

            var currentUri: Uri?
            var path = "<Dateifehler>"

            data?.let {
                currentUri = it.data
                val metaCursor = contentResolver.query(currentUri!!, null, null, null, null)
                if (metaCursor != null) {
                    try {
                        if (metaCursor.moveToFirst()) {
                            path = currentUri!!.path!!
                            //path = metaCursor.getString(metaCursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                            //path = metaCursor.getString(metaCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            //path = metaCursor.getString(0)
                            Log.d("test3", path)
                        }
                    } finally {
                        metaCursor.close();
                    }
                }

            }
            Log.d("test1:", selectedFile.toString())

            //val atest2: Uri = selectedFile!!
            //val file = DocumentFile.fromSingleUri(MainActivity.context,selectedFile)
            //val fileName = file.getName()
            //val test = selectedFile!!.toString()
            //var fileTypeDoc: List<String> = selectedFile!!.path!!.split(";")
            //val pfadmitTypundDoc = selectedFile!!.path
            val pfadarray = path.split(":")
            val pfadvariable = findViewById<EditText>(R.id.DateiName)
            Log.d("TAG Size:", pfadarray.size.toString())
            if (pfadarray.size > 1) {
                pfadvariable.setText(pfadarray[1])
            } else {
                pfadvariable.setText(path)
            }
            /*if (!pfadarray[1].isEmpty()) {
                pfadvariable.setText(pfadarray[1])
            } else {
                pfadvariable.setText("Datei nicht gefunden!")
            }*/
        }
    }

    override fun onResume() {
        super.onResume()
        //val getDateiFromPfad = MainActivity.PfadZurDatei.split("/")
        val pfadvariable = findViewById<EditText>(R.id.DateiName)
        pfadvariable.setText(MainActivity.PfadZurDatei)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val pfadvariable = findViewById<EditText>(R.id.DateiName)
        MainActivity.PfadZurDatei = pfadvariable.getText().toString();

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return false
        with (sharedPref.edit()) {
            putBoolean("AlleFertigkeitenAnzeigen_SharedPref", MainActivity.alleFertigkeitenAnzeigen)
            putString("PfadZurDatei_SharedPref",MainActivity.PfadZurDatei)

            commit()
        }

        return true
    }

}
