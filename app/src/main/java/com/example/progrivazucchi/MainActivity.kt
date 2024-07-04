package com.example.progrivazucchi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.media.MediaRecorder
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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date

const val REQUEST_CODE =200
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), Tempo.OnTimerTickListener{

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
    private lateinit var db: RegistrazioniAudioSQLiteHelper
    private lateinit var vibrazione: Vibrator




    override fun onCreate(savedInstanceState: Bundle?) {
        db = RegistrazioniAudioSQLiteHelper(this)           //avvio db
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        //controllo se l'utente ha accettato i permessi
        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)



        val included = findViewById<LinearLayout>(R.id.bottomSheetIncluder)
        val bottomSheetBehavior = BottomSheetBehavior.from(included)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        tempo = Tempo(this)
        vibrazione = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        //funzione per la gestione del bottone di registrazione
        findViewById<ImageButton>(R.id.btnRegistra).setOnClickListener{
            when {
                inPausa -> tornaARegistrare()
                staRegistrando -> fermaRegistrazione()
                else -> inizioRegistrazione()
            }
            vibrazione.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }


        //funzione per la gestione del bottone elenco
        findViewById<ImageButton>(R.id.btnElenco).setOnClickListener{
            startActivity(Intent(this, Galleria::class.java))
        }



        //funzione per la gestione del bottone fatto
        findViewById<ImageButton>(R.id.btnFatto).setOnClickListener {
            fermaRegistrare()
            Toast.makeText(this, "opzioni di salvataggio", Toast.LENGTH_SHORT).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            findViewById<View>(R.id.BottomSheetBackGround).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.bottomSheetIncluder).visibility = View.VISIBLE
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

            rimozione() // toglie il bottomSheet
            salvataggio() // salva il file nell'archivio
        }


        //"contorno" del bottomsheet
        findViewById<View>(R.id.BottomSheetBackGround).setOnClickListener {
            File("$dirPath$nomeFile.mp3").delete()
            rimozione()
        }


//        funzione per la gestione del bottone cancella
        findViewById<ImageButton>(R.id.btnCancella).setOnClickListener {
            fermaRegistrare()
            File("$dirPath$nomeFile.mp3")
            Toast.makeText(this, "registrazione eliminata", Toast.LENGTH_SHORT).show()
        }

        // btnCancella non cliccabile in questo momento
        findViewById<ImageButton>(R.id.btnCancella).isClickable = false
    }




    // funzione per la gestione del salvataggio
    private fun salvataggio(){
        val nuovoNomeFile = findViewById<TextView>(R.id.inputNomeFile).text.toString()
        if(nuovoNomeFile != nomeFile){
            val nuovoFile = File("$dirPath$nuovoNomeFile.mpeg")
            File("$dirPath$nomeFile.mpeg").renameTo(nuovoFile)
        }
        val filePath = "$dirPath$nuovoNomeFile.mpeg"
        val timestamp = Date().time
        val ampsPath = "$dirPath$nuovoNomeFile"
        try {
            val fos = FileOutputStream(ampsPath)
            val out = ObjectOutputStream(fos)
            fos.close()
            out.close()
        }catch (_:IOException){}
        db = RegistrazioniAudioSQLiteHelper(this)
        db.inserisciRegistrazione(RegistrazioniAudio(nuovoNomeFile,filePath,timestamp,duration))
    }



    // funzione per la gestione della rimozione
    private fun rimozione(){
        findViewById<View>(R.id.BottomSheetBackGround).visibility= View.GONE
        nascondiKeyboard(findViewById<TextView>(R.id.inputNomeFile))
        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<LinearLayout>(R.id.bottomSheetIncluder).visibility = View.GONE
        }, 100)
    }



    // funzione utile per nascondere la tastiera
    private fun nascondiKeyboard(view: View ){
        val inputm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputm.hideSoftInputFromWindow(view.windowToken, 0)

    }

    // funzione per la gestione dei permessi
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
        tempo.pausa()
    }


    // funzione per la gestione del bottone di pausa
    private fun tornaARegistrare(){
        recorder.resume()
        inPausa = false
        findViewById<ImageButton>(R.id.btnRegistra).setImageResource(R.drawable.ic_pausa)
        tempo.avvio()
    }


    // funzione per la gestione del bottone di inizio
    private fun inizioRegistrazione() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }
        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"
        val simpleDateFormat = SimpleDateFormat("dd.mm.yyyy_hh.mm.ss")
        val date = simpleDateFormat.format(Date())
        nomeFile = "audio_record-$date"


        // impostazioni di registrazione o setUp
        recorder.apply{
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) //Advanced Audio Coding (AAC) è un formato di compressione audio digitale
            setOutputFile("$dirPath$nomeFile.mpeg")
            try {
                prepare()
            }catch (_: IOException){}
            start()
        }

        // settaggio bottone di pausa
        findViewById<ImageButton>(R.id.btnRegistra).setImageResource(R.drawable.ic_pausa)
        staRegistrando = true
        inPausa = false
        tempo.avvio()

        // il bottone cancella è cliccabile
        findViewById<ImageButton>(R.id.btnCancella).isClickable = true


        findViewById<ImageButton>(R.id.btnCancella).setImageResource(R.drawable.baseline_delete_24)

        // in questo momento/il bottone elenco non è visibile
        findViewById<ImageButton>(R.id.btnElenco).visibility = View.GONE


        // il bottone fatto è visibile
        findViewById<ImageButton>(R.id.btnFatto).visibility = View.VISIBLE
    }

    // funzione per fermare la registrazione
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
    }



    // quando viene dato il via al timer, questa funzione fa partire il tempo e lo ferma
    override fun onTimerTick(duration: String) {
        val esecuzione = findViewById<TextView>(R.id.cronometro)
        esecuzione.text = duration
        this.duration = duration.dropLast(3)
    }
}



