package com.example.progrivazucchi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.core.app.ActivityCompat
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


const val REQUEST_CODE =200
class MainActivity : AppCompatActivity(), Tempo.OnTimerTickListener {

    private lateinit var ampiezza: ArrayList<Float>

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

    private lateinit var tempo: Tempo

    private lateinit var vibrazione: Vibrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE) //all'utente verra presentata la
        //interfaccia utente per richiedere i permessi, successivamente verrà informato se sono stati accettati

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
            //TODO
            Toast.makeText(this, "bottone elenco", Toast.LENGTH_SHORT).show()
            // messaggio mostrato quando si schiaccia sul bottone elenco


        }

        findViewById<ImageButton>(R.id.btnFatto).setOnClickListener(){
            fermaRegistrare()
            //TODO
            Toast.makeText(this, "registrazione salvata", Toast.LENGTH_SHORT).show()
            // messaggio mostrato quando si schiaccia sul bottone salvataggio
        }

        findViewById<ImageButton>(R.id.btnCancella).setOnClickListener(){
            fermaRegistrare()
            File("$dirPath$nomeFile.mp3") // formato nome del file
            Toast.makeText(this, "registrazione eliminata", Toast.LENGTH_SHORT).show()
            // messaggio mostrato quando si schiaccia sul bottone cancella
        }

        findViewById<ImageButton>(R.id.btnCancella).isClickable = false // btnCancella non
        // cliccabile in questo momento

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

        //ampiezza = findViewById<com.example.progrivazucchi.FormaOnda>(R.id.forma_onda).clear()

    }

    // quando viene dato il via al timer, questa funzione fa partire il tempo e lo ferma
    override fun onTimerTick(duration: String) {
         val esecuzione = findViewById<TextView>(R.id.cronometro)
         esecuzione.text = duration
         var onda: FormaOnda = findViewById(R.id.forma_onda)
         onda.aggiungiAmpiezza(recorder.maxAmplitude.toFloat())

        //findViewById<FormaOnda>(R.id.forma_onda).aggiungiAmpiezza(recorder.maxAmplitude.toFloat())     //aggiorna la forma d'onda
    }
}



