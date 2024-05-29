package com.example.progrivazucchi

import android.content.Intent
import android.media.AudioRecord
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Galleria : AppCompatActivity(), OnItemClickListener {
    private lateinit var records : ArrayList<RegistratoreAudio>
    private lateinit var myAdapter : Adattatore
    private lateinit var database: AppDatabase
    private lateinit var ricerca_input : TextInputEditText
    private lateinit var toolbar : MaterialToolbar
    private lateinit var editBar: View
    private lateinit var btnChiuso: ImageButton
    private lateinit var btnSelezionaTutto: ImageButton
    private var allChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galleria)

        toolbar = findViewById(R.id.toolbar)                //funzioni per chiamare la toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        editBar = findViewById(R.id.editBar)
        btnChiuso = findViewById(R.id.btnChiuso)
        btnSelezionaTutto = findViewById(R.id.btnSelezionaTutto)

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
        btnChiuso.setOnClickListener {                                            //se viene premuto il pulsante chiuso, ripristina la lista
            supportActionBar?.setDisplayShowTitleEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(true)
            editBar.visibility = View.GONE
            records.map { it.isCheck = false }
            myAdapter.setEditMode(false)
        }
        btnSelezionaTutto.setOnClickListener {                                    //se viene premuto il pulsante seleziona tutto, seleziona tutti i record
            allChecked = !allChecked
            records.map { it.isCheck = allChecked }
            myAdapter.notifyDataSetChanged()
        }
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
    override fun onItemClickListener(position: Int) {               //permette di selezionare tramite check il record ed i record presenti
        var audioRecord = records[position]

        if (myAdapter.isEditMode()){
            records[position].isCheck = !records[position].isCheck
            myAdapter.notifyItemChanged(position)
        }else{
            var intent  = Intent(this, LettoreAudio::class.java)
            intent.putExtra("filepath", audioRecord.filepath)
            intent.putExtra("nomefile", audioRecord.nomefile)
            startActivity(intent)
        }


    }

    // Nel momento in cui si tiene premuto su un elemento, diventa visibile il
    // Toast "Click lungo"
    override fun onItemLongClickListener(position: Int) {
        myAdapter.setEditMode(true)
        records[position].isCheck = !records[position].isCheck
        myAdapter.notifyItemChanged(position)

        if (myAdapter.isEditMode() && editBar.visibility == View.GONE){
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            editBar.visibility = View.VISIBLE
        }
    }

}