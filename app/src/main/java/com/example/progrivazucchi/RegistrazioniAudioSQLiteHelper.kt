package com.example.progrivazucchi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import android.database.sqlite.SQLiteQuery
import androidx.core.content.ContextCompat.startActivity


class RegistrazioniAudioSQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "registrazioneAudio.db"
        private const val DATABASE_VERSION = 1
        const val table_name = "registrazioni_salvate"
        private const val column_name = "nomefile"
        private const val column_filepath = "filepath"
        private const val column_timestamp = "timestamp"
        private const val column_duration = "duration"
    }



    //funzione per la ricerca
    fun searchDatabase(criterium: String): Cursor? {
        val db = this.readableDatabase
        var query = "SELECT * FROM $table_name "
        if (!query.isEmpty()){
            query += "WHERE $column_name LIKE '%$criterium%'"
        }
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        return cursor
    }



    //funzione per la creazione
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $table_name ($column_name TEXT PRIMARY KEY, $column_filepath TEXT, $column_timestamp TEXT, $column_duration TEXT)"
        db?.execSQL(createTableQuery)
    }


    //funzione per la modifica
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $table_name"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }


    //funzione l'inserimento
    fun inserisciRegistrazione(registrazioniAudio: RegistrazioniAudio){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(column_name, registrazioniAudio.nomefile)
            put(column_filepath, registrazioniAudio.filepath)
            put(column_timestamp, registrazioniAudio.timestamp)
            put(column_duration, registrazioniAudio.duration)
        }
        db.insertOrThrow(table_name, null, values)
        db.close()
    }


    //funzione per l'aggiornamento
    fun aggiornaRegistrazione(registrazioniAudio: RegistrazioniAudio, nomefile: String){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(column_name, nomefile)
            put(column_filepath, registrazioniAudio.filepath)
            put(column_timestamp, registrazioniAudio.timestamp)
            put(column_duration, registrazioniAudio.duration)
        }
        db.update(table_name, values, "$column_name = ?", arrayOf(registrazioniAudio.nomefile))
    }



    //funzione per la cancellazione
    fun cancella(registrazioniAudio: Array<RegistrazioniAudio>){
        val db = writableDatabase
        for (registrazioniAudio in registrazioniAudio){
            db.delete(table_name, "$column_name = ?", arrayOf(registrazioniAudio.nomefile))
        }
    }

}