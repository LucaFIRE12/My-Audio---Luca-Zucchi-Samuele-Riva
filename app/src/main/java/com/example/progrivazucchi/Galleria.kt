package com.example.progrivazucchi

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


@Suppress("DEPRECATION")
class Galleria : AppCompatActivity(), OnItemClickListener {
    private lateinit var records : List<RegistrazioniAudio>
    private lateinit var myAdapter : Adattatore
    private lateinit var ricerca_input : TextInputEditText
    private lateinit var toolbar : MaterialToolbar
    private lateinit var database : RegistrazioniAudioSQLiteHelper
    private lateinit var editBar: View
    private lateinit var btnChiuso: ImageButton
    private lateinit var btnSelezionaTutto: ImageButton
    private lateinit var btnDeselezionaTutto: ImageButton
    private lateinit var icSelezionaTutto: ImageView
    private var allChecked = false
    private var noneChecked = true
    private lateinit var bottomSheet : LinearLayout
    private lateinit var bottomSheetBehavior : BottomSheetBehavior<LinearLayout>
    private lateinit var btnModifica: ImageButton
    private lateinit var btnElimina: ImageButton
    private lateinit var textModifica: TextView
    private lateinit var textElimina: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galleria)
        database = RegistrazioniAudioSQLiteHelper(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        ricerca_input = findViewById<TextInputEditText>(R.id.ricerca_input)

        //contenuto della barra di ricerca
        ricerca_input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mostraElencoRegistrazioni(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

        btnModifica = findViewById(R.id.btnModifica)
        btnElimina = findViewById(R.id.btnElimina)
        textModifica = findViewById(R.id.textModifica)
        textElimina = findViewById(R.id.textElimina)

        editBar = findViewById(R.id.editBar)
        btnChiuso = findViewById(R.id.btnChiuso)
        bottomSheet = findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        btnSelezionaTutto = findViewById(R.id.btnSelezionatutto)
        btnDeselezionaTutto = findViewById(R.id.btnDeselezionatutto)
        icSelezionaTutto = findViewById(R.id.iconaSelezionaTutto)
        records = ArrayList()
        myAdapter = Adattatore(records, this)



        //se viene premuto il pulsante chiuso, ripristina la lista
        btnChiuso.setOnClickListener {
            myAdapter.setEditMode(false)
            esciEditMode()
            mostraElencoRegistrazioni("")
        }


        //se viene premuto il pulsante, deseleziona tutti i record
        btnDeselezionaTutto.setOnClickListener {
            Toast.makeText(this, "Deseleziona tutto", Toast.LENGTH_SHORT).show()
            mostraElencoRegistrazioni("")
            records.map { it.isChecked = !allChecked }
            myAdapter.notifyDataSetChanged()
            disabilitaModifica()
            abilitaElimina()
            icSelezionaTutto.visibility = View.INVISIBLE
            btnSelezionaTutto.visibility = View.VISIBLE
            btnDeselezionaTutto.visibility = View.INVISIBLE
            btnChiuso.performClick()
        }


        //se viene premuto il pulsante seleziona tutto, seleziona tutti i record
        btnSelezionaTutto.setOnClickListener {
            Toast.makeText(this, "Seleziona tutto", Toast.LENGTH_SHORT).show()
            mostraElencoRegistrazioni("")
            records.map { it.isChecked = !allChecked }
            myAdapter.notifyDataSetChanged()
            disabilitaModifica()
            abilitaElimina()
            icSelezionaTutto.visibility = View.VISIBLE
            btnSelezionaTutto.visibility = View.INVISIBLE
            btnDeselezionaTutto.visibility = View.VISIBLE
        }


        //elimina i record selezionati
        btnElimina.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Vuoi Eliminare i record selezionati?")
            val nbRecords = records.count { it.isChecked }
            if (nbRecords == 0) {
                Toast.makeText(this, "Nessun record selezionato", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            builder.setMessage("Sei sicuro di voler eliminare $nbRecords record(s)?")
            builder.setNegativeButton("Si"){ _, _ ->            //scelte dell'utente
                val eliminare = records.filter { it.isChecked }.toTypedArray()
                GlobalScope.launch {
                    database.cancella(eliminare)
                    runOnUiThread{
                        (records as ArrayList<RegistrazioniAudio>).removeAll(eliminare.toList().toSet())
                        myAdapter.notifyDataSetChanged()
                        mostraElencoRegistrazioni("")       //quando viene terminata l'eliminazione aggiorna la lista
                        esciEditMode()
                    }
                }
            }
            builder.setPositiveButton("No"){ _, _ ->
            }
            val dialog = builder.create()
            dialog.show()
        }


        //modifica i record selezionati
        btnModifica.setOnClickListener{

            val nbRecords = records.count { it.isChecked }
            if (nbRecords == 0) {
                Toast.makeText(this, "Nessun record selezionato", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(nbRecords > 1){
                Toast.makeText(this, "Solo un record può essere modificato", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.rinomina_layout, null)
            builder.setView(dialogView)
            val dialog = builder.create()
            val record = records.filter{it.isChecked}[0]
            val textInput = dialogView.findViewById<TextInputEditText>(R.id.inputNomeFile)
            textInput.setText(record.nomefile)


            //Dopo che viene scelto un record da modificare,
            //cliccando il pulsante salva essa viene salvata nel db
            dialogView.findViewById<Button>(R.id.btnSalva).setOnClickListener{
                val input = textInput.text.toString()
                if(input.isEmpty()){
                    Toast.makeText(this, "Il nome non può essere vuoto", Toast.LENGTH_LONG).show()
                }else{
                    GlobalScope.launch {
                        database.aggiornaRegistrazione(record, input)
                        runOnUiThread{
                            myAdapter.notifyItemChanged(records.indexOf(record))
                            myAdapter.notifyDataSetChanged()
                            mostraElencoRegistrazioni("")
                            dialog.dismiss()
                            esciEditMode()
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            btnModifica.visibility = View.GONE
                            btnElimina.visibility = View.GONE
                            textModifica.visibility = View.GONE
                            textElimina.visibility = View.GONE
                            bottomSheet.visibility = View.GONE
                        }
                    }
                }
            }
            dialogView.findViewById<Button>(R.id.btnAnnulla).setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
        mostraElencoRegistrazioni("")
    }

    //disabilita la funzione di modifica e ripristina la toolbar
    private fun esciEditMode(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        editBar.visibility = View.GONE
        //chiamati sia hidden che collapsed per far sparire il bottom sheet del tutto
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        records.map { it.isChecked = false }
        myAdapter.setEditMode(false)
        myAdapter.notifyDataSetChanged()
    }


    //funzioni per rendere visibili o meno il pulsante modifica
    private fun disabilitaModifica(){
        btnModifica.isClickable = false
        btnModifica.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.grayDark, theme)
        textModifica.setTextColor(ResourcesCompat.getColor(resources, R.color.grayDark, theme))
    }

    //funzioni per rendere visibili o meno il pulsante elimina
    private fun disabilitaElimina(){
        btnElimina.isClickable = false
        btnElimina.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.grayDark, theme)
        textElimina.setTextColor(ResourcesCompat.getColor(resources, R.color.grayDark, theme))
    }


    //funzioni per rendere visibili o meno il pulsante modifica
    private fun abilitaModifica(){
        btnModifica.isClickable = true
        btnModifica.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.BackGroundOpaco, theme)
        textModifica.setTextColor(ResourcesCompat.getColor(resources, R.color.BackGroundOpaco, theme))
    }

    //funzioni per rendere visibili o meno il pulsante elimina
    private fun abilitaElimina(){
        btnElimina.isClickable = true
        btnElimina.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.BackGroundOpaco, theme)
        textElimina.setTextColor(ResourcesCompat.getColor(resources, R.color.BackGroundOpaco, theme))
    }



    //funzione che mostra la lista dei record aggiornati al termine di ogni operazione
    fun mostraElencoRegistrazioni(datoRicerca : String)
    {
        val recyclerView = findViewById<RecyclerView>(R.id.listaRegistrazioniRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val cursor = database.searchDatabase(datoRicerca)
        val listaRegistrazioni = cursorToRegistrazioniAudio(cursor)
        records = listaRegistrazioni
        val adapter = Adattatore(listaRegistrazioni,this)
        recyclerView.adapter=adapter
        adapter.notifyDataSetChanged()
    }



    //funzione che converte il cursor in RegistrazioniAudio
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



    // Nel momento in cui si tiene premuto su un elemento, diventa visibile il
    // Toast "Click semplice" permette di selezionare tramite check il record ed i record presenti
    override fun onItemLongClickListener(position: Int) {
        val audioRecord = records[position]

        if(icSelezionaTutto.visibility == View.VISIBLE){
            Toast.makeText(this, "Tenere premuto per selezionare", Toast.LENGTH_SHORT).show()
            return
        }
        if(records[position].isChecked){
            noneChecked= false
        }
        if (myAdapter.isEditMode()){
            icSelezionaTutto.visibility = View.INVISIBLE
            records[position].isChecked = !records[position].isChecked
            myAdapter.notifyItemChanged(position)
            val selected = records.count { it.isChecked }
            when(selected){
                0 -> {
                    btnChiuso.performClick()
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
    override fun onItemClickListener(position: Int) {
        val pos = position
        if(icSelezionaTutto.visibility == View.VISIBLE){
            icSelezionaTutto.visibility = View.INVISIBLE
            btnSelezionaTutto.visibility = View.VISIBLE
            btnDeselezionaTutto.visibility = View.INVISIBLE
            records.map { it.isChecked = allChecked }
            myAdapter.notifyDataSetChanged()
            onItemClickListener(pos)
            return
        }
        icSelezionaTutto.visibility = View.INVISIBLE
        myAdapter.setEditMode(true)
        records[position].isChecked = !records[position].isChecked
        myAdapter.notifyItemChanged(position)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (myAdapter.isEditMode() && editBar.visibility == View.GONE){
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            editBar.visibility = View.VISIBLE
            abilitaModifica()
            abilitaElimina()
        }
    }
}