package com.example.progrivazucchi

import android.content.Context
import android.Manifest
import android.media.AudioRecord
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.text.SimpleDateFormat
import java.util.*
import java.sql.Timestamp
import java.util.Date
import kotlinx.datetime.Clock




// utile a mappare il layout di itemview, quindi conosce cosa contengono i layout delle registrazioni,
// come settare i valori che le riguardano e como interagire con essi
class Adattatore(var records : ArrayList<RegistratoreAudio>) : RecyclerView.Adapter<ViewHolder>() {

    // collega ogni singolo elemento del layout itemview a una variabile
    inner class GestoreView(itemView : View) : RecyclerView.ViewHolder(itemView){
        var tvNomeFile : TextView = itemView.findViewById<TextView>(R.id.tvNomeFile)
        var tvMeta : TextView = itemView.findViewById<TextView>(R.id.tvMeta)
        var checkBox : CheckBox = itemView.findViewById(R.id.checkbox)
    }

    // ritorna un oggetto ViewHolder fornendowli una vista, ovvero il contenuto del
    // layout itemview
    // ViewHolder: involucro attorno alla vista che contiene il layout per un elemento particolare
    // nella List<AudioRecorder>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemview, parent, false)
        return GestoreView(view)

    }

    // ritorna il numero di elementi (items) nella recyclerView ed è usato per stabilire quando
    // terminare la visualizzazione di nuovi elementi
    // RecyclerView: contiene le viste corrispondenti ai dati
    override fun getItemCount(): Int {
        return records.size
    }

    // funzione che viene chiamata dalla recyclerView per associare un ViewHolder a un istanza di
    // dati
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // quando cerchiamo di creare items quando la RecycleReview sta ancora caricando
        if(position != RecyclerView.NO_POSITION){
            var record = records[position]

            var sdf = SimpleDateFormat("dd/MM/yyyy")
            var date = Date(record.timestamp)
            var strDate = sdf.format(date)
            holder.itemView.findViewById<TextView>(R.id.tvNomeFile).text = record.nomefile
            holder.itemView.findViewById<TextView>(R.id.tvMeta).text = "${record.duration} $strDate"
        }
    }

}