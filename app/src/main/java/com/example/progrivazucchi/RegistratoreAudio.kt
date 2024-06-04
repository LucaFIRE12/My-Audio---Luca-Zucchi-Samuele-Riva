package com.example.progrivazucchi

import androidx.room.Entity
import androidx.room.Ignore
import java.sql.Timestamp
import java.time.Duration
import androidx.room.PrimaryKey as PrimaryKey1

//classe per istanziare le funzioni base del db
//db = database

@Entity(tableName = "registratoreAudio")
data class  RegistratoreAudio(       //tutto l'occorrente che server per l'esecuzione del db
    var nomefile: String,
    var filepath: String,
    var timestamp: Long,
    var duration: String
){                                          // chiave primaria
    @PrimaryKey1(autoGenerate = true)
    var id = 0
    @Ignore
    var isCheck = false

}
