package com.example.progrivazucchi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import android.database.sqlite.SQLiteQuery


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

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $table_name ($column_name TEXT PRIMARY KEY, $column_filepath TEXT, $column_timestamp TEXT, $column_duration TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $table_name"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

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

    fun aggiornaRegistrazione(registrazioniAudio: RegistrazioniAudio){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(column_name, registrazioniAudio.nomefile)
            put(column_filepath, registrazioniAudio.filepath)
            put(column_timestamp, registrazioniAudio.timestamp)
            put(column_duration, registrazioniAudio.duration)
        }
        db.update(table_name, values, "$column_name = ?", arrayOf(registrazioniAudio.nomefile))
        db.close()
    }
    fun prendiTutto(): List<RegistrazioniAudio> {
        val db = readableDatabase
        val cursor = db.query(table_name, null, null, null, null, null, null)
        val registrazioniAudioList = mutableListOf<RegistrazioniAudio>()
        cursor.moveToFirst()
        if(cursor.count != 0)
        do{
            // posso tradurre le colonne del cursore con il nome della colonna
            // cursor.get... prende il nome della colonna e restituisce il relativo indice

            // sapendo già come è strutturato il cursor è più sensato utilizzare
            // gli indici interi diretti anzichè utilizzare getColumn.. e sopprimere
            // l'errore relativo al -1
            val nomefile = cursor.getString(0)//cursor.getColumnIndex(column_name)
            val filepath = cursor.getString(1)//cursor.getColumnIndex(column_filepath)
            val timestamp = cursor.getLong(2)//cursor.getColumnIndex(column_timestamp)
            val duration = cursor.getString(3)//cursor.getColumnIndex(column_duration)
            registrazioniAudioList.add(RegistrazioniAudio(nomefile, filepath, timestamp, duration))
        }while(cursor.moveToNext())
        cursor.close()
        db.close()
        return registrazioniAudioList
    }

    fun cancella(registrazioniAudio: Array<RegistrazioniAudio>){
        val db = writableDatabase
        db.delete(table_name, "$column_name = ?", arrayOf(arrayOf(registrazioniAudio).toString()))
        db.close()
    }
}