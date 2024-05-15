package com.example.progrivazucchi

import android.media.AudioRecord
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

// utile a mappare il layout di itemview, quindi conosce cosa contengono i layout delle registrazioni,
// come settare i valori che le riguardano e como interagire con essi
class Adattatore(var records : List<AudioRecord>) : RecyclerView.Adapter<ViewHolder> {

    inner class GestoreView(itemView : View) : RecyclerView.ViewHolder(itemView){
        var tvNomeFile : TextView = itemView.findViewById<TextView>(R.id.tvNomeFile)
        var tvMeta : TextView = itemView.findViewById<TextView>(R.id.tvMeta)
        var checkBox : CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}