package com.example.progrivazucchi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RegistrazioniAudioDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "registrazioneAudio.db"
        private const val DATABASE_VERSION = "1"
        private const val table_name = "registrazioni_salvate"
        private const val COLOUMN_name = "nomefile"
        private const val COLOUMN_filepath = "filepath"
        private const val collumn_timestamp = "timestamp"
        private const val collumn_duration = "duration"
        private const val collumn_ampsPath = "ampsPath"




    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $table_name ($COLOUMN_name TEXT PRIMARY KEY, $COLOUMN_filepath TEXT, $collumn_timestamp TEXT, $collumn_duration TEXT, $collumn_ampsPath TEXT)"
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
            put(COLOUMN_name, registrazioniAudio.nomefile)
            put(COLOUMN_filepath, registrazioniAudio.filepath)
            put(collumn_timestamp, registrazioniAudio.timestamp)
            put(collumn_duration, registrazioniAudio.duration)
            put(collumn_ampsPath, registrazioniAudio.ampsPath)
        }
        db.insert(table_name, null, values)
        db.close()
    }

    fun aggiornaRegistrazione(registrazioniAudio: RegistrazioniAudio){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLOUMN_name, registrazioniAudio.nomefile)
            put(COLOUMN_filepath, registrazioniAudio.filepath)
            put(collumn_timestamp, registrazioniAudio.timestamp)
            put(collumn_duration, registrazioniAudio.duration)
        }
    }


}