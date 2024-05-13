package com.example.progrivazucchi

import androidx.room.Database
import androidx.room.RoomDatabase            //db = database
@Database(entities = arrayOf(RegistratoreAudio::class), version = 1)            //contenitore dei record del db
abstract class AppDatabase : RoomDatabase() {
    abstract fun registratoreAudioDao() : RegistratoreAudioDao
}