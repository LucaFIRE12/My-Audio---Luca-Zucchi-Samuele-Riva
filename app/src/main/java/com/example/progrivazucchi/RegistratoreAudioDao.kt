package com.example.progrivazucchi

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


                                //CANCELLARE TUTTO IL FILE IN MANIERA INDISCRIMINATA NONAPPENA SI SARA' CREATO UN RIMPIAZZO
                                //LA CLASSE DAO NON è PIU' SUPPORTATA



@Dao
            //db = database
            //Dao = Data Access Objects, aiuta la realizzazione e lo svolgimento del db usando metodi e funzioni al posto delle query
interface RegistratoreAudioDao {
    @Query("SELECT * FROM registratoreAudio")           //query, che mostra tutte le registrazioni presenti nel db
    fun prendiTutto(): List<RegistratoreAudio>          //restituisce una lista di Registratore audio

    @Query("SELECT * FROM registratoreAudio WHERE nomefile LIKE :query")           //query, che mostra tutte le registrazioni presenti nel db
    fun searchDatabase(query: String): List<RegistratoreAudio>

    @Insert
    fun insert(vararg registratoreAudio: RegistratoreAudio)      //fa INSERT INTO del db

    @Delete
    fun cancella(registratoreAudio: RegistratoreAudio)

    @Delete
    fun cancella(registratoreAudio: Array<RegistratoreAudio>)

    @Update
    fun aggiorna(registratoreAudio: Array<out RegistratoreAudio>)           //fa UPDATE del db "colonna"


}