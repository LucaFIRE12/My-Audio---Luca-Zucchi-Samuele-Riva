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
    private var ampiezze = ArrayList<Float>()   //ampiezza del suono
    private var picchi = ArrayList<RectF>()   //picchi del suono

    private var raggio = 6f //parametri per dare forma alle punte dei picchi
    private var w = 9f
    private var d = 6f  //distanza tra rettangoli

    private var sw = 0f     //width forma d'onda
    private var sh = 400f   //heigh forma d'onda

    //private var maxPicchi = 0f


    init {
        colore.color = Color.rgb(244,81,30)

        sw = resources.displayMetrics.widthPixels.toFloat()

        //maxPicchi = (sw/(w+d)).toInt().toFloat()
    }

    //funzione che mostra a livello grafico l'intensità del suono, più una barra è ampia e più è alto di decibel
    //i valori vengono salvati dentro un arraylist, che scorre durante la fase di registrazione
    fun aggiungiAmpiezza(amp: Float){
        //var normalizza = Math.min(amp.toInt()/7, 400).toFloat()
        ampiezze.add(amp)
        picchi.clear()
        //var ampiezze = ampiezza.takeLast(maxPicchi.toInt())

        //parametri per dare forma alle punte dei picchi
        for (i in ampiezze.indices){
            var sx = sw - i*(w+d)           // distanza dal bordo sinistro del telefono
            var sopra = 7f                 // altezza della barra -> quello che dovrebbe variare in base all'uso di record
            var dx = sx + w                 // base della barra
            var sotto = ampiezze[i]
            picchi.add(RectF(sx, sopra, dx, sotto))         // punte dei picchi
        }
        invalidate()
    }

    // funzione che serve a dare una forma alle "barre" della forma d'onda della traccia audio
    //includendo, punto di inizio, fine, tipo di barra ed arrotondamento
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        picchi.forEach{
            canvas?.drawRoundRect(it, raggio, raggio, colore)        //allineamento dei picchi, formati dalla funzione aggiungiAmpiezza
        }
    }
}