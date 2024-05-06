package com.example.progrivazucchi

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import android.widget.ImageButton
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


const val REQUEST_CODE =200
class MainActivity : AppCompatActivity() {
    //richiesta dei permessi necessari
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO) // creazione di un array di permessi richiesti al manifest file ,contenente info sul
    // progetto ed in grado di rilasciare permessi che vanno esplicitamente richiesti nel codice. il risultato è una finestra in app che richiede all'utente un
    // permesso a cui lui decide se dare il consenso
    val btnRegistra = findViewById<ImageButton>(R.id.btnRegistra)

    private var permissionGranted = false

    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var nomeFile = "" //inizializzazione nome di un file audio generico
    private var staRegistrando = false
    private var inPausa = false
    var simpleDateFormat = SimpleDateFormat("GG.MM.AAAA_hh.mm.ss") // costruzione formato di una data relativa
    // a un salvataggio
    var date = simpleDateFormat.format(Date())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE) //all'utente verra presentata la
            //interfaccia utente per richiedere i permessi, successivamente verrà informato se sono stati accettati

        btnRegistra.setOnClickListener{
            when {
                inPausa -> tornaARegistrare()
                staRegistrando -> fermaRegistrazione()
                else -> inizioRegistrazione()
            }
        }
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
        btnRegistra.setImageResource(R.drawable.ic_registra)
    }

    private fun tornaARegistrare(){
        recorder.resume()
        inPausa = false
        btnRegistra.setImageResource(R.drawable.ic_pausa)
    }

    private fun inizioRegistrazione() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }

        // inizio processo di registrazione audio

        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"


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

        btnRegistra.setImageResource(R.drawable.ic_pausa) // settaggio bottone di pausa
        staRegistrando = true
        inPausa = false
    }
}