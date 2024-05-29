package com.example.progrivazucchi

import android.content.Intent
import android.media.AudioRecord
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Galleria : AppCompatActivity(), OnItemClickListener {
    private lateinit var records : ArrayList<RegistratoreAudio>
    private lateinit var myAdapter : Adattatore
    private lateinit var database: AppDatabase
    private lateinit var ricerca_input : TextInputEditText

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

        fetchAll()

        ricerca_input = findViewById(R.id.ricerca_input)                //barra di ricerca
        ricerca_input.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {             //query di ricerca
                var query = s.toString()
                searchDatabase(query)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun searchDatabase(query: String) {             //funzione per la query di ricerca, dove trova tutti i nomi simili a ciò che abbiamo messo
        GlobalScope.launch {
            records.clear();
            var queryResult = database.registratoreAudioDao().searchDatabase("%$query%")
            records.addAll(queryResult)
            runOnUiThread{
                myAdapter.notifyDataSetChanged()
            }
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
        var audioRecord = records[position]
        var intent  = Intent(this, LettoreAudio::class.java)
        intent.putExtra("filepath", audioRecord.filepath)
        intent.putExtra("nomefile", audioRecord.nomefile)
        startActivity(intent)
    }

    // Nel momento in cui si tiene premuto su un elemento, diventa visibile il
    // Toast "Click lungo"
    override fun onItemLongClickListener(position: Int) {
        Toast.makeText(this, "Click lungo", Toast.LENGTH_SHORT).show()
    }

}