package com.example.progrivazucchi

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class FormaOnda(context: Context?, attrs: AttributeSet?): View(context, attrs) {



    private var colore = Paint()    //colori impiegati nella forma d'onda
    private var ampiezza = ArrayList<Float>()   //ampiezza del suono
    private var picchi = ArrayList<RectF>()   //picchi del suono

    private var raggio = 6f //parametri per dare forma alle punte dei picchi
    private var w = 9f  // larghezza rettangoli
    private var d = 6f  //distanza tra rettangoli

    private var sw = 0f     //width forma d'onda
    private var sh = 400f   //heigh forma d'onda

    private var piccoMassimo = 0

    init {
        colore.color = Color.rgb(244,81,30)

        sw = resources.displayMetrics.widthPixels.toFloat()

        piccoMassimo = (sw / (w+d)).toInt()
    }

    //funzione che mostra a livello grafico l'intensità del suono, più una barra è ampia e più è alto di decibel
    //i valori vengono salvati dentro un arraylist, che scorre durante la fase di registrazione
    fun aggiungiAmpiezza(amp: Float){
        var norm = Math.min(amp.toInt()/7, 400).toFloat()  //normalizzazione valori (400:dimensione schermo)
        ampiezza.add(norm)


        picchi.clear()
        var ampl = ampiezza.takeLast(piccoMassimo) // variabile che indica l'altezza massima
        // di un'onda
        for (i in ampl.indices){
            var sx = sw - i*(w+d)       //parametri per dare forma alle punte dei picchi
            var sopra = sh/2 - ampl[i]/2
            var dx = sx + d
            var sotto = top + ampl[i]
            picchi.add(RectF(sx, sopra, dx, sotto))         // punte dei picchi
        }

        invalidate()
    }

    // funzione che serve a dare una forma alle "barre" della forma d'onda della traccia audio
    //includendo, punto di inizio, fine, tipo di barra ed arrotondamento
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        //canvas?.drawRoundRect(RectF(60f, 60f, 60+80f, 60f+360f), 6f, 6f, colore)
        picchi.forEach{
            canvas?.drawRoundRect(it, raggio, raggio, colore)        //allineamento dei picchi, formati dalla funzione aggiungiAmpiezza
        }
    }
}