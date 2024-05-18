package com.example.progrivazucchi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.app.ActivityCompat
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import androidx.room.Room

// RIGHE 40, 67 E 179
const val REQUEST_CODE =200
class MainActivity : AppCompatActivity(), Tempo.OnTimerTickListener {

    // SE TOLGO LATEINIT E LO PONGO = arrayListOf()
    // NON DA PIù ERRORRE QUA MA ALLA RIGA 203 bottomSheetBehavior
    // DICE SEMPRE CHE MANCA LA DICHIARAZIONE, PROBABILMENTE ALLA RIGA 67
    // prima c'era leteinit e non ? = null
    private var ampiezza: ArrayList<Float>? = null

    //richiesta dei permessi necessari
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO) // creazione di un array di permessi richiesti al manifest file ,contenente info sul
    // progetto ed in grado di rilasciare permessi che vanno esplicitamente richiesti nel codice. il risultato è una finestra in app che richiede all'utente un
    // permesso a cui lui decide se dare il consenso


    private var permissionGranted = false


    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var nomeFile = "" //inizializzazione nome di un file audio generico
    private var staRegistrando = false
    private var inPausa = false

    private var duration = ""

    private lateinit var tempo: Tempo

    private lateinit var db: AppDatabase

    private lateinit var vibrazione: Vibrator

    // riferimento al LinearLayout di bottom_sheet.xml
    // SE TOLGO LATEINIT L'APP NON CRASHA AL SALVATAGGIO
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE) //all'utente verra presentata la
        //interfaccia utente per richiedere i permessi, successivamente verrà informato se sono stati accettati

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "registratoreAudio"
        ).build()

        val included = findViewById<LinearLayout>(R.id.bottomSheetIncluder)
        val bottomSheetBehavior = BottomSheetBehavior.from(included)
        bottomSheetBehavior.peekHeight = 0 // in questo modo quando il layout bottom_sheet verrà chiuso
        // sara completamente invisibile

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED // il bottonm_sheet viene qui
        // definito effettivamente collassato

        //richiama il metodo OnTimerTickListener sia da questa classe che tutto ciò che è presnet dentro al file Tempo.kt
        tempo = Tempo(this)
        vibrazione = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        findViewById<ImageButton>(R.id.btnRegistra).setOnClickListener{
            when {
                inPausa -> tornaARegistrare()
                staRegistrando -> fermaRegistrazione()
                else -> inizioRegistrazione()
            }

            vibrazione.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }

        findViewById<ImageButton>(R.id.btnElenco).setOnClickListener{
            startActivity(Intent(this, Galleria::class.java))


        }

        findViewById<ImageButton>(R.id.btnFatto).setOnClickListener {
            fermaRegistrare()
            Toast.makeText(this, "registrazione salvata", Toast.LENGTH_SHORT).show()
            // messaggio mostrato quando si schiaccia sul bottone salvataggio

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED // in questo modo lo stato del
            // bottom_sheet è definito come "espanso"

            findViewById<View>(R.id.BottomSheetBackGround).visibility = View.VISIBLE
            // il bottom sheet è visibile nel momento in cui si preme il bottone btnFatto

            findViewById<TextView>(R.id.inputNomeFile).text = nomeFile


        }


        // quando il bottone viene premuto il back ground viene reso non visibile e il bottomsheet
        // viene collassato
        findViewById<Button>(R.id.btnElimina).setOnClickListener{
            File("$dirPath$nomeFile.mp3").delete()
            rimozione()
        }

        //qui viene nascosto il bottomsheet e implementato il salvataggio
        findViewById<Button>(R.id.BtnOk).setOnClickListener{

            rimozione()
            salvataggio()
        }

        findViewById<View>(R.id.BottomSheetBackGround).setOnClickListener {
            File("$dirPath$nomeFile.mp3").delete()
            rimozione()
        }

        findViewById<ImageButton>(R.id.btnCancella).setOnClickListener {
            fermaRegistrare()
            File("$dirPath$nomeFile.mp3") // formato nome del file
            Toast.makeText(this, "registrazione eliminata", Toast.LENGTH_SHORT).show()
            // messaggio mostrato quando si schiaccia sul bottone cancella

        }

        findViewById<ImageButton>(R.id.btnCancella).isClickable = false // btnCancella non
        // cliccabile in questo momento

    }



    @OptIn(DelicateCoroutinesApi::class)
    private fun salvataggio(){
        //rinominazione di un file

        // creazione nuovo nome
        val nuovoNomeFile = findViewById<TextView>(R.id.inputNomeFile).text.toString()
        if(nuovoNomeFile != nomeFile){
            var nuovoFile = File("$dirPath$nuovoNomeFile.mp3") // creazione nuovo file
            File("$dirPath$nomeFile.mp3").renameTo(nuovoFile) //rinominazione
        }

        var filePath = "$dirPath$nuovoNomeFile.mp3"     //salvataggio del db
        var timestamp = Date().time
        var ampsPath = "$dirPath$nuovoNomeFile"

        // VA AGGIUNTO SE SI TOGLIE LATEINIT ALLA DICHIARAZIONE DI AMPIEZZA
        // ampiezza = arrayListOf()

        try {
            var fos = FileOutputStream(ampsPath)
            var out = ObjectOutputStream(fos)
            out.writeObject(ampiezza)           //salva la forma d'onda del file
            fos.close()
            out.close()
        }catch (e :IOException){}

        var registrazione = RegistratoreAudio(nomeFile,filePath,timestamp,duration,ampsPath)


                    //per il salvataggio, viene creato un thread che lavora in background apposta
        GlobalScope.launch {
            db.registratoreAudioDao().insert(registrazione)      //salvataggio nel server di una registrazione appena effettuata
        }
    }

    private fun rimozione(){
        // eliminazione visibilità del background
        findViewById<View>(R.id.BottomSheetBackGround).visibility= View.GONE
        nascondiKeyboard(findViewById<TextView>(R.id.inputNomeFile))
        Handler(Looper.getMainLooper()).postDelayed({

            this.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 100) // la funzione attende 100 milliSecondi e nasconde il bottomSheet
    }

    // funzione utile per nascondere la tastiera
    private fun nascondiKeyboard(view: View ){
        val inputmm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // input method manager, gestisce le comunicazioni tra processi
        inputmm.hideSoftInputFromWindow(view.windowToken, 0) // nasconde la tastiera

    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        //i permessi concessi dovrebbero corrispondere
        // al grantResult (risposta alla richiesta sviluppata dall'utente) che riceviamo di conseguenza
        //è necessario avere un permesso ogni volta che si vuole iniziare a registrare
    }

    // funzione per la gestione del bottone di registrazione, con controllo dei permessi
    private fun fermaRegistrazione(){
        recorder.pause()
        inPausa = true
        findViewById<ImageButton>(R.id.btnRegistra).setImageResource(R.drawable.ic_registra)

        tempo.pausa() //metodo presente dentro la classe Tempo.kt
    }

    private fun tornaARegistrare(){
        recorder.resume()
        inPausa = false
        findViewById<ImageButton>(R.id.btnRegistra).setImageResource(R.drawable.ic_pausa)

        tempo.stop() //metodo presente dentro la classe Tempo.kt
    }


    private fun inizioRegistrazione() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }

        // inizio processo di registrazione audio




        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"

        var simpleDateFormat = SimpleDateFormat("dd.mm.yyyy_hh.mm.ss") // costruzione formato di una data relativa
        // a un salvataggio
        var date = simpleDateFormat.format(Date())


        nomeFile = "audio_record-$date"

        // impostazioni di registrazione o setUp
        recorder.apply{
            setAudioSource(MediaRecorder.AudioSource.MIC)
            // set utili per salvare i file in formato mp3
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) //Advanced Audio Coding (AAC) è un formato di compressione audio digitale
            setOutputFile("$dirPath$nomeFile.mp3")

            try {
                prepare()

            }catch (e: IOException){}

            start()
        }

        // settaggio bottone di pausa
        findViewById<ImageButton>(R.id.btnRegistra).setImageResource(R.drawable.ic_pausa)


        staRegistrando = true
        inPausa = false
        tempo.avvio() //metodo presente dentro la classe Tempo.kt

        findViewById<ImageButton>(R.id.btnCancella).isClickable = true // il bottone cancella
        // è cliccabile
        findViewById<ImageButton>(R.id.btnCancella).setImageResource(R.drawable.baseline_delete_24)

        findViewById<ImageButton>(R.id.btnElenco).visibility = View.GONE // in questo momento
        //il bottone elenco non è visibile
        findViewById<ImageButton>(R.id.btnFatto).visibility = View.VISIBLE // in questo momento
        //il bottone fatto è visibile

    }

    private fun fermaRegistrare(){


        tempo.stop()

        recorder.apply {
            stop()
            release()
        }

        inPausa = false
        staRegistrando = false

        findViewById<ImageButton>(R.id.btnElenco).visibility = View.VISIBLE // bottone elenco visibile
        findViewById<ImageButton>(R.id.btnFatto).visibility = View.GONE // bottone fatto non visibile

        findViewById<ImageButton>(R.id.btnCancella).isClickable = false // tasto cancella non cliccabile
        findViewById<ImageButton>(R.id.btnCancella).setImageResource(R.drawable.baseline_delete_24_disabled)
        findViewById<ImageButton>(R.id.btnRegistra).setImageResource(R.drawable.ic_registra)

        findViewById<TextView>(R.id.cronometro).text = "00:00.00"


        // ho messo questa funzione come commento perchè sennò non runna nulla
        //ampiezza = findViewById<FormaOnda>(R.id.forma_onda).clear()

    }

    // quando viene dato il via al timer, questa funzione fa partire il tempo e lo ferma
    override fun onTimerTick(duration: String) {
        val esecuzione = findViewById<TextView>(R.id.cronometro)
        esecuzione.text = duration
        var onda: FormaOnda = findViewById(R.id.forma_onda)
        onda.aggiungiAmpiezza(recorder.maxAmplitude.toFloat())

        //if(permissionGranted){
          //  Toast.makeText(this, recorder.maxAmplitude.toString(), Toast.LENGTH_SHORT).show()

        //}else{
          //  Toast.makeText(this, "Errore, non viene ricevuto nessun segnale audio", Toast.LENGTH_SHORT).show()
        //}



        this.duration = duration.dropLast(3)
    }
}



