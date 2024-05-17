package com.example.progrivazucchi

import android.media.AudioRecord
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Galleria : AppCompatActivity(), OnItemClickListener {
    private lateinit var records : ArrayList<RegistratoreAudio>
    private lateinit var myAdapter : Adattatore
    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galleria)

        records = ArrayList()
        database = databaseBuilder(
            this,
            AppDatabase::class.java,
            "registratoreAudio"
        ).build()
        myAdapter = Adattatore(records, this)

        // la recyclerView necessita di due informazioni: 1)un adattatore per sapere come deve
        // apparire, come deve comportarsi, quali dati mostrare e di 2)un layoutManager che gli dica
        // come posizionare gli items e riciclare quelli che non sono sullo schermo

        findViewById<RecyclerView>(R.id.recyclerview).apply {
            var adapter = myAdapter
            var LayoutManager = LinearLayoutManager(context)
        }
    }
    //metodo che aggiorna il sistema dell'aggiornamento del db e inserimento della query
    private fun fetchAll(){
        GlobalScope.launch {
            records.clear();
            var queryResult = database.registratoreAudioDao().prendiTutto()
            records.addAll(queryResult)
            myAdapter.notifyDataSetChanged()
        }
    }

    // Nel momento in cui si tiene premuto su un elemento, diventa visibile il
    // Toast "Click semplice"
    override fun onItemClickListener(position: Int) {
        Toast.makeText(this, "Click semplice", Toast.LENGTH_SHORT).show()
    }

    // Nel momento in cui si tiene premuto su un elemento, diventa visibile il
    // Toast "Click lungo"
    override fun onItemLongClickListener(position: Int) {
        Toast.makeText(this, "Click lungo", Toast.LENGTH_SHORT).show()
    }

}