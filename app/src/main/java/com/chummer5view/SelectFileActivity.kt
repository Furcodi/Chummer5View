package com.chummer5view

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import kotlinx.android.synthetic.main.activity_selectfile.*
import android.widget.LinearLayout




class SelectFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectfile)
//        val actionbar = supportActionBar
//        actionbar!!.title = "Select File"

        var list = ArrayList<String>()

        findSAFs(File("storage/emulated/0"), list)

        list.forEach() { dateiname ->
            val textfeld0 = TextView(this)

            with(textfeld0)
            {
                text = dateiname
                textSize = 15f
                paintFlags = Paint.UNDERLINE_TEXT_FLAG
                //textColor = Color.parseColor("#1F2135")
                typeface = Typeface.DEFAULT_BOLD
               // isClickable = true
                setOnClickListener {
                    MainActivity.PfadZurDatei = textfeld0.text.toString()
                    finish() }
            }

            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(60, 30, 0, 30)
            textfeld0.setLayoutParams(params)
/**            textfeld0.setText(dateiname)
            //textfeld0.setTextSize(3.0);
//            textfeld0.setTag("" + i)
            textfeld0.setLines(1)
            textfeld0.setOnClickListener(View.OnClickListener {
                MainActivity.PfadZurDatei = textfeld0.text.toString()
                finish()
            })
**/
            mainText.addView(textfeld0)
         }

    }


    fun findSAFs(dir: File, matchingSAFFileNames: ArrayList<String>): ArrayList<String> {
        val safPattern = ".xml"

        val listFile = dir.listFiles()

        if (listFile != null) {
            for (i in listFile.indices) {

                if (listFile[i].isDirectory()) {
                    findSAFs(listFile[i], matchingSAFFileNames)
                } else {
                    if (listFile[i].getName().endsWith(safPattern)) {
                        matchingSAFFileNames.add(dir.toString() + File.separator + listFile[i].getName())
                        Log.d("FileName: ",dir.toString() + File.separator + listFile[i].getName())
                    }
                }
            }
        }
        return matchingSAFFileNames
    }

}