package com.example.progrivazucchi

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
            //db = database
            //Dao = Data Access Objects, aiuta la realizzazione e lo svolgimento del db usando metodi e funzioni al posto delle query
interface RegistratoreAudioDao {
    @Query("SELECT * FROM registratoreAudio")           //query, che mostra tutte le registrazioni presenti nel db
    fun prendiTutto(): List<RegistratoreAudio>          //restituisce una lista di Registratore audio

    @Insert
    fun insert(vararg registratoreAudio: RegistratoreAudio)      //fa INSERT INTO del db

    @Delete
    fun cancella(registratoreAudio: RegistratoreAudio)

    @Delete
    fun cancella(registratoreAudio: Array<RegistratoreAudio>)

    @Update
    fun aggiorna(registratoreAudio: Array<out RegistratoreAudio>)           //fa UPDATE del db "colonna"


}