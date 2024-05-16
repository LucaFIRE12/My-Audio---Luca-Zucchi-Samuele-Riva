package com.example.progrivazucchi

import android.media.AudioRecord
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter



class Galleria : AppCompatActivity() {
    private lateinit var records : ArrayList<RegistratoreAudio>
    private lateinit var myAdapter : Adattatore
    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galleria)

        records = ArrayList()
        myAdapter = Adattatore(records)

        // la recyclerView necessita di due informazioni: 1)un adattatore per sapere come deve
        // apparire, come deve comportarsi, quali dati mostrare e di 2)un layoutManager che gli dica
        // come posizionare gli items e riciclare quelli che non sono sullo schermo

        findViewById<ConstraintLayout>(R.id.recyclerview).apply {
            //adapter = myAdapter
        }
    }
}