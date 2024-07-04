package com.example.progrivazucchi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date




// utile a mappare il layout di itemview, quindi conosce cosa contengono i layout delle registrazioni,
// come settare i valori che le riguardano e como interagire con essi
class Adattatore(var registrazione : List<RegistrazioniAudio>, var listener: OnItemClickListener) : RecyclerView.Adapter<ViewHolder>() {

    // collega ogni singolo elemento del layout itemview a una variabile
    // View.OnClickListener: per lanciare la player Activity tramite tocco di un bottone
    // View.OnLongClickListener: Utilizzato per riscontare quando l'utente
    // mantiene il tocco su una vista per un periodo più lungo del semplice click
    private var editMode = false
    private var count = 0



    fun isEditMode(): Boolean {return editMode}
    fun setEditMode(mode: Boolean){             //funzione che permette di salvare la modifica effettuata al record salvato
        if (editMode != mode){
            editMode = mode
            notifyDataSetChanged()
        }

    }


    inner class GestoreView(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener{

        var tvNomeFile : TextView = itemView.findViewById<TextView>(R.id.tvNomeFile)
        var tvMeta : TextView = itemView.findViewById<TextView>(R.id.tvMeta)
        //var checkBox : CheckBox = itemView.findViewById(R.id.checkbox)
        val iconaV : ImageView = itemView.findViewById<ImageView>(R.id.iconaV)


        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        // quando un elemento viene cliccato viene chiamata la funzione
        // onItemClickListener di Galleria.kt

        override fun onLongClick(v: View?): Boolean {

            val position = adapterPosition


            setEditMode(true)

            if(iconaV.visibility == View.INVISIBLE){
                iconaV.visibility = View.VISIBLE
            }
            else{
                iconaV.visibility = View.INVISIBLE
            }

            if(position != RecyclerView.NO_POSITION ){
                listener.onItemClickListener(position)
                return true
            }




            /*val position = adapterPosition
            setEditMode(true)



            if(checkBox.isChecked){
                count--
                checkBox.isChecked = false
                if(count == 0){
                    setEditMode(false)

                }
            }else{
                checkBox.isChecked = true
                count++
            }

            if(position != RecyclerView.NO_POSITION ){
                listener.onItemClickListener(position)
                return true
            }
            */
            return true


        }



        // quando un elemento viene cliccato viene chiamata la funzione
        // onItemLongClickListener di Galleria.kt
        override fun onClick(v: View?) {

            val position = adapterPosition

            if(editMode==true){
                if(iconaV.visibility == View.INVISIBLE){
                    iconaV.visibility = View.VISIBLE
                }
                else{
                    iconaV.visibility = View.INVISIBLE

                }
            }

            if(position != RecyclerView.NO_POSITION ){
                listener.onItemClickListener(position)
            }


            /*
            val position = adapterPosition

            if(editMode){
                if(checkBox.isChecked){
                    count--
                    checkBox.isChecked = false
                    if(count == 0){
                        setEditMode(false)

                    }
                }else{
                    checkBox.isChecked = true
                    count++
                }
            }

            if(position != RecyclerView.NO_POSITION ){
                listener.onItemClickListener(position)
            }


             */
        }



    }



    // ritorna un oggetto ViewHolder fornendogli una vista, ovvero il contenuto del
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
        return registrazione.size
    }

    // funzione che viene chiamata dalla recyclerView per associare un ViewHolder a un istanza di
    // dati
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // quando cerchiamo di creare items quando la RecycleReview sta ancora caricando
        if(position != RecyclerView.NO_POSITION){
            var record = registrazione[position]
            var sdf = SimpleDateFormat("dd/MM/yyyy")
            var date = Date(record.timestamp)
            var strDate = sdf.format(date)
            holder.itemView.findViewById<TextView>(R.id.tvNomeFile).text = record.nomefile
            holder.itemView.findViewById<TextView>(R.id.tvMeta).text = "${record.duration} $strDate"
                                //se la funzione editMode è attiva, checbox diventa visibile e l'item viene selezionato

            /*
            if(editMode){
                holder.itemView.findViewById<CheckBox>(R.id.checkbox).visibility = View.VISIBLE
                holder.itemView.findViewById<CheckBox>(R.id.checkbox).isChecked   // = true
            }else{
                holder.itemView.findViewById<CheckBox>(R.id.checkbox).visibility = View.GONE
                holder.itemView.findViewById<CheckBox>(R.id.checkbox).isChecked = false
            }

             */



        }
    }

    fun setData(registrazione: ArrayList<RegistrazioniAudio>){
        this.registrazione = registrazione
        notifyDataSetChanged()
    }


}