package com.example.progrivazucchi

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



@Suppress("DEPRECATION")
class Galleria : AppCompatActivity(), OnItemClickListener {
    private lateinit var records : ArrayList<RegistrazioniAudio>
    private lateinit var myAdapter : Adattatore
    private lateinit var ricerca_input : TextInputEditText
    private lateinit var toolbar : MaterialToolbar
    private lateinit var database : RegistrazioniAudioSQLiteHelper
    private lateinit var editBar: View
    private lateinit var btnChiuso: ImageButton
    private lateinit var btnSelezionaTutto: ImageButton
    private var allChecked = false
    private lateinit var bottomSheet : LinearLayout
    private lateinit var bottomSheetBehavior : BottomSheetBehavior<LinearLayout>
    private lateinit var btnModifica: ImageButton
    private lateinit var btnElimina: ImageButton
    private lateinit var textModifica: TextView
    private lateinit var textElimina: TextView


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

        btnModifica = findViewById(R.id.btnModifica)
        btnElimina = findViewById(R.id.btnElimina)
        textModifica = findViewById(R.id.textModifica)
        textElimina = findViewById(R.id.textElimina)

        editBar = findViewById(R.id.editBar)
        btnChiuso = findViewById(R.id.btnChiuso)
        btnSelezionaTutto = findViewById(R.id.btnSelezionaTutto)
        bottomSheet = findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        records = ArrayList()
        myAdapter = Adattatore(records, this)


        // la recyclerView necessita di due informazioni: 1)un adattatore per sapere come deve
        // apparire, come deve comportarsi, quali dati mostrare e di 2)un layoutManager che gli dica
        // come posizionare gli items e riciclare quelli che non sono sullo schermo



        //fetchAll()                <-- questa funzione non si capisce che fa ma sopratutto se integrata non si può accedere all'elenco

        ricerca_input = findViewById(R.id.ricerca_input)                //barra di ricerca
        ricerca_input.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {             //query di ricerca
                val query = s.toString()
                searchDatabase(query)

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        btnChiuso.setOnClickListener {                                            //se viene premuto il pulsante chiuso, ripristina la lista
            esciEditMode()
        }

        btnSelezionaTutto.setOnClickListener {                                    //se viene premuto il pulsante seleziona tutto, seleziona tutti i record

            allChecked = !allChecked
            records.map { it.isChecked = allChecked }
            myAdapter.notifyDataSetChanged()

            if (allChecked){
                disabilitaModifica()
                abilitaElimina()
            }else{
                disabilitaModifica()
                disabilitaElimina()
            }
        }
        btnElimina.setOnClickListener {                                             //elimina i record selezionati
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Vuoi Eliminare i record selezionati?")
            val nbRecords = records.count { it.isChecked }
            builder.setMessage("Sei sicuro di voler eliminare $nbRecords record(s)?")

            builder.setPositiveButton("Si"){ _, _ ->            //scelte dell'utente

                val eliminare = records.filter { it.isChecked }.toTypedArray()
                GlobalScope.launch {
                    database.cancella(eliminare)
                    runOnUiThread{
                        records.removeAll(eliminare.toList().toSet())
                        myAdapter.notifyDataSetChanged()
                        esciEditMode()
                    }
                }
            }
            builder.setPositiveButton("No"){ _, _ ->

            }        //questo non fa nulla

            val dialog = builder.create()
            dialog.show()
        }
        findViewById<ImageButton>(R.id.btnModifica).setOnClickListener{
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.rinomina_layout, null)
            builder.setView(dialogView)
            val dialog = builder.create()
            val record = records.filter{it.isChecked}[0]
            // cerco nel dialogView un TextInputEditText con id inputNomeFile
            val textInput = dialogView.findViewById<TextInputEditText>(R.id.inputNomeFile)
            textInput.setText(record.nomefile)

            dialogView.findViewById<Button>(R.id.btnSalva).setOnClickListener{
                val input = textInput.text.toString()
                if(input.isEmpty()){
                    Toast.makeText(this, "Il nome non può essere vuoto", Toast.LENGTH_LONG).show()
                }else{
                    record.nomefile = input // aggiorno il nomefile
                    GlobalScope.launch {
                        database.aggiornaRegistrazione(record)
                        runOnUiThread{
                            myAdapter.notifyItemChanged(records.indexOf(record))
                            dialog.dismiss()
                            esciEditMode()
                        }
                    }
                }
            }

            dialogView.findViewById<Button>(R.id.btnAnnulla).setOnClickListener{
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun esciEditMode(){             //disabilita la funzione di modifica e ripristina la toolbar
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        editBar.visibility = View.GONE
        //chiamati sia hidden che collapsed per far sparire il bottom sheet del tutto
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        records.map { it.isChecked = false }
        myAdapter.setEditMode(false)
    }
                                //funzioni per rendere visibili o meno i pulsanti modifica
    private fun disabilitaModifica(){
        btnModifica.isClickable = false
        btnModifica.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.grayDark, theme)
        textModifica.setTextColor(ResourcesCompat.getColor(resources, R.color.grayDark, theme))

    }
    private fun disabilitaElimina(){
        btnElimina.isClickable = false
        btnElimina.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.grayDark, theme)
        textElimina.setTextColor(ResourcesCompat.getColor(resources, R.color.grayDark, theme))

    }

    private fun abilitaModifica(){
        btnModifica.isClickable = false
        btnModifica.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.BackGroundOpaco, theme)
        textModifica.setTextColor(ResourcesCompat.getColor(resources, R.color.BackGroundOpaco, theme))

    }
    private fun abilitaElimina(){
        btnElimina.isClickable = false
        btnElimina.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.BackGroundOpaco, theme)
        textElimina.setTextColor(ResourcesCompat.getColor(resources, R.color.BackGroundOpaco, theme))

    }

    // COM'ERA PRIMA
    /*
    private fun searchDatabase(query: String) {             //funzione per la query di ricerca, dove trova tutti i nomi simili a ciò che abbiamo messo
        GlobalScope.launch {
            records.clear()
            val queryResult = database.searchDatabase(query)     //"SELECT * FROM ${RegistrazioniAudioSQLiteHelper.table_name} WHERE nomefile LIKE '%$query%'"
            records.addAll(queryResult)
            runOnUiThread{
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    */

    // COM'è ORA

    private fun searchDatabase(query: String): Collection<RegistrazioniAudio> {
        if (query.isEmpty()) {
            return listOf() // Return an empty list if the query is empty
        }

        // Perform the database query and get the Cursor
        val cursor = database.searchDatabase(query)

        // Convert the Cursor to a Collection<RegistrazioniAudio>
        val queryResult = cursorToRegistrazioniAudio(cursor)

        // Update the records list with the query results
        GlobalScope.launch {
            records.clear()
            records.addAll(queryResult)

            // Update the adapter on the main thread
            runOnUiThread {
                myAdapter.notifyDataSetChanged()
            }
        }

        return queryResult
    }

    fun cursorToRegistrazioniAudio(cursor: Cursor?): List<RegistrazioniAudio> {
        val result = mutableListOf<RegistrazioniAudio>()
        if (cursor != null) {
            while (cursor.moveToNext()) {

                val nomefile = cursor.getString(cursor.getColumnIndexOrThrow("nomefile"))
                val filepath = cursor.getString(cursor.getColumnIndexOrThrow("filepath"))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                val duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"))

                val registrazione = RegistrazioniAudio(nomefile, filepath, timestamp, duration)
                result.add(registrazione)
            }
            cursor.close()
        }
        return result
    }


    //metodo che aggiorna il sistema dell'aggiornamento del db e inserimento della query
    private fun fetchAll(){
        GlobalScope.launch {
            records.clear()
            val queryResult = database.prendiTutto()
            records.addAll(queryResult)
            myAdapter.notifyDataSetChanged()
        }
    }

    // Nel momento in cui si tiene premuto su un elemento, diventa visibile il
    // Toast "Click semplice"
    override fun onItemClickListener(position: Int) {               //permette di selezionare tramite check il record ed i record presenti
        val audioRecord = records[position]

        if (myAdapter.isEditMode()){
            records[position].isChecked = !records[position].isChecked
            myAdapter.notifyItemChanged(position)

            val selected = records.count { it.isChecked }
            when(selected){
                0 -> {
                    disabilitaModifica()
                    disabilitaElimina()
                }
                1 -> {
                    abilitaModifica()
                    abilitaElimina()
                }
                else -> {
                    disabilitaModifica()
                    abilitaElimina()
                }
            }
        }else{
            val intent  = Intent(this, LettoreAudio::class.java)
            intent.putExtra("filepath", audioRecord.filepath)
            intent.putExtra("nomefile", audioRecord.nomefile)
            startActivity(intent)
        }


    }

    // Nel momento in cui si tiene premuto su un elemento, diventa visibile il
    // Toast "Click lungo"
    override fun onItemLongClickListener(position: Int) {
        myAdapter.setEditMode(true)
        records[position].isChecked = !records[position].isChecked
        myAdapter.notifyItemChanged(position)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED      //viene avviato il bottom sheet

        if (myAdapter.isEditMode() && editBar.visibility == View.GONE){
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            editBar.visibility = View.VISIBLE

            abilitaModifica()
            abilitaElimina()
        }
    }

}