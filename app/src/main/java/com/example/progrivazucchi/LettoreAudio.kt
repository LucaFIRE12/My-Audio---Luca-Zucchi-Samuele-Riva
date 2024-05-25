package com.example.progrivazucchi

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip

class LettoreAudio : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlay: ImageButton
    private lateinit var btnIndietroSec: ImageButton
    private lateinit var btnAvantiSec: ImageButton
    private lateinit var chip: Chip
    private lateinit var seekBar: SeekBar

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var ritardo = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lettore_audio_)
        var filepath = intent.getStringExtra("filepath")
        var nomefile = intent.getStringExtra("nomefile")

        mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setDataSource(filepath)
            prepare()
        }


        btnIndietroSec = findViewById(R.id.btnIndietroSec)
        btnAvantiSec = findViewById(R.id.btnAvantiSec)
        btnPlay = findViewById(R.id.btnPlay)
        chip = findViewById(R.id.chip)
        seekBar = findViewById(R.id.seekBar)

        handler = Handler(Looper.getMainLooper())           //permette alla barra di scorrere in base allo stato di proseguimento del player
        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentPosition
            handler.postDelayed(runnable, ritardo)                  //input lag simulato "necessario per evitare problemi coi processi"
        }

        btnPlay.setOnClickListener{
            playPausePlayer()
        }
        playPausePlayer()
        seekBar.max = mediaPlayer.duration

        mediaPlayer.setOnCompletionListener {
            btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_circle, theme)
            handler.removeCallbacks(runnable)

        }
    }

    private fun playPausePlayer(){              //funzione per far partire il player
        if(mediaPlayer.isPlaying){
            mediaPlayer.start()
            btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_pause_circle, theme)
            handler.postDelayed(runnable, 0)
        }else{
            mediaPlayer.pause()
            btnPlay.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_circle, theme)
            handler.removeCallbacks(runnable)
        }

    }
}