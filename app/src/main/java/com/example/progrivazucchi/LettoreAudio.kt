package com.example.progrivazucchi

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.PlaybackParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip

class LettoreAudio : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlay: ImageButton
    private lateinit var btnIndietroSec: ImageButton
    private lateinit var btnAvantiSec: ImageButton
    private lateinit var chip: Chip
    private lateinit var seekBar: SeekBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var mostraNomeFile: TextView
    private lateinit var progresso: TextView
    private lateinit var durata: TextView


    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var ritardo = 1000L
    private var jumpValue = 1000
    private var playVelocitaIndietro = 1.0f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lettore_audio_)
        val filepath = intent.getStringExtra("filepath")
        val nomefile = intent.getStringExtra("nomefile")

        toolbar = findViewById(R.id.toolbar)
        mostraNomeFile = findViewById(R.id.mostraNomeFile)
        progresso = findViewById(R.id.mostraProgresso)
        durata = findViewById(R.id.mostraDurata)



        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()

        }

        mediaPlayer = MediaPlayer()             //inizializzo il player
        mediaPlayer.apply {
            setDataSource(filepath)
            prepare()
        }

        mostraNomeFile.text = nomefile              //mostra il nome del file


        durata.text = formatTime(mediaPlayer.duration)                   //converte il minutaggio in stringa da mostrare in textview

        btnIndietroSec = findViewById(R.id.btnIndietroSec)
        btnAvantiSec = findViewById(R.id.btnAvantiSec)
        btnPlay = findViewById(R.id.btnPlay)
        chip = findViewById(R.id.chip)
        seekBar = findViewById(R.id.seekBar)

        handler = Handler(Looper.getMainLooper())           //permette alla barra di scorrere in base allo stato di proseguimento del player
        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentPosition
            progresso.text = formatTime(mediaPlayer.currentPosition)         //converte il minutaggio in stringa da mostrare in textview
            handler.postDelayed(runnable, ritardo)                  //input lag simulato "necessario per evitare problemi coi processi"
        }

        btnPlay.setOnClickListener{
            playPausePlayer()
        }
        playPausePlayer()
        // ogni posizione del seekBar progress si ricollega
        // ad una posizione del mediaPlayer
        seekBar.max = mediaPlayer.duration


        mediaPlayer.setOnCompletionListener {
            btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_circle, theme)
            handler.removeCallbacks(runnable)
            resetToolbar()

        }

        // premendo il bottone btnAvantiSec la registrazione del player si muove di 1 secondo
        findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.btnAvantiSec).setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition + jumpValue)
            seekBar.progress += jumpValue

        }

        findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.btnIndietroSec).setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition - jumpValue)
            seekBar.progress -= jumpValue
        }

        // bottone moltiplicazione di velocità di riproduzione delle registrazioni
        chip.setOnClickListener{
            if(playVelocitaIndietro != 2.0f){
                // se la velocità di riproduzione è diversa da 2 la
                // velocità di riproduzione aumenta di 0.5f
                playVelocitaIndietro += 0.5f
            }
            else{
                // se la velocità di riproduzione è uguale a 2 la
                // velocità di riproduzione ritorna a 0.5f
                playVelocitaIndietro = 0.5f
            }
            mediaPlayer.playbackParams = PlaybackParams().setSpeed(playVelocitaIndietro)
            chip.text = "x $playVelocitaIndietro"
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            // fromUser : se il cambiamento è stato effettuato dall'utente o no
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // se è inizializzato dall'utente:
                if(fromUser){
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun playPausePlayer(){              //funzione per far partire il player
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start() // avvio
            btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_pause_circle, theme)
            handler.postDelayed(runnable, ritardo)
        }else{
            mediaPlayer.pause() // pausa
            btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_circle, theme)
            handler.removeCallbacks(runnable)
        }
    }

    override fun onBackPressed() {          //mostra il bottone poer tornare indietro dalla riproduzione all'elenco delle registrazioni
        super.onBackPressed()
        //mediaPlayer.stop()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }

    fun formatTime(timeInMillis: Int): String {             //funzione per formattare il tempo in stringa
        val totalSeconds = timeInMillis / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun resetToolbar() {            //funzione per resettare il toolbar
        seekBar.progress= 0
        progresso.text = "00:00"

    }


}