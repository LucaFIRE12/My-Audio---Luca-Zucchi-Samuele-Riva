package com.example.progrivazucchi

import android.os.Handler
import android.os.Looper

class Tempo(listener: OnTimerTickListener) {
    interface OnTimerTickListener{
        fun onTimerTick(duration: String)
    }

    private var handler = Handler(Looper.getMainLooper()!!)
    private lateinit var runnable: Runnable

    // 100L -> 100 ms
    private var durata = 0L
    private var ritardo = 100L

    //quando viene attivata la funzione "avvio", viene fatto aspettare un minimo, prima di poter
    //restituire a schermo il risultato
    init {
        runnable = Runnable{
            durata+=ritardo
            handler.postDelayed(runnable, ritardo)
            listener.onTimerTick(formattaTempo())
        }
    }

    //funzione che attiva il runnable
    fun avvio(){
        handler.postDelayed(runnable, ritardo)
    }

    fun pausa(){
        handler.removeCallbacks(runnable)
    }

    fun stop(){
        handler.removeCallbacks(runnable)
        durata = 0L
    }

    //funzione che serve per mostrare il tempo effettivo sul display
    private fun formattaTempo():String{
        val ms = durata % 1000
        val sec = (durata/1000) % 60
        val min = (durata/(1000*60)) % 60
        val hrs = (durata/(1000*60*60))

        val tempoFormattato = if(hrs>0)
            "%02d:%02d:%02d.%02d".format(hrs, min, sec, ms) //%02 -> il risultato restituiscilo con 2 cifre
        else
            "%02d:%02d.%02d".format(min, sec, ms)
        return tempoFormattato

    }
}