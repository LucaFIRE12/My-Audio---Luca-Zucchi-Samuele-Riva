package com.example.progrivazucchi


data class RegistrazioniAudio(var nomefile: String, var filepath: String, var timestamp: Long, var duration: String) {
    // aggiunto per utilizzarlo in galleria

    var isChecked = false
}