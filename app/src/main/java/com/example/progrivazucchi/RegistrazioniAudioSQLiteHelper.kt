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
        private const val COLOUMN_name = "nomefile"
        private const val COLOUMN_filepath = "filepath"
        private const val collumn_timestamp = "timestamp"
        private const val collumn_duration = "duration"




    }
    fun searchDatabase(query: String): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $table_name"
        val cursor = db.rawQuery(query, null)
        return cursor
    }




    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $table_name ($COLOUMN_name TEXT PRIMARY KEY, $COLOUMN_filepath TEXT, $collumn_timestamp TEXT, $collumn_duration TEXT)"
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
    @SuppressLint("Range")
    fun prendiTutto(): List<RegistrazioniAudio> {
        val db = readableDatabase
        val cursor = db.query(table_name, null, null, null, null, null, null)
        val registrazioniAudioList = mutableListOf<RegistrazioniAudio>()
        while (cursor.moveToNext()) {

            val nomefile = cursor.getString(cursor.getColumnIndex(COLOUMN_name))
            val filepath = cursor.getString(cursor.getColumnIndex(COLOUMN_filepath))
            val timestamp = cursor.getLong(cursor.getColumnIndex(collumn_timestamp))
            val duration = cursor.getString(cursor.getColumnIndex(collumn_duration))
            registrazioniAudioList.add(RegistrazioniAudio(nomefile, filepath, timestamp, duration))
        }
        cursor.close()
        db.close()
        return registrazioniAudioList
    }



    fun cancella(registrazioniAudio: Array<RegistrazioniAudio>){
        val db = writableDatabase
        db.delete(table_name, "$COLOUMN_name = ?", arrayOf(arrayOf(registrazioniAudio).toString()))
        db.close()
    }




}