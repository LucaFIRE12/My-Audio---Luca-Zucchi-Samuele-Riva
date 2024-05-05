package com.example.progrivazucchi

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.btnRegistra

const val REQUEST_CODE =200
class MainActivity : AppCompatActivity() {
    //richiesta dei permessi necessari
    private var permissions = arrayOf(Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION)

    private var permissionGranted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRegistra = findViewById<Button>(R.id.btnRegistra)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE) //all'utente verra presentata la
            //interfaccia utente per richiedere i permessi, successivamente verrà informato se sono stati accettati

        btnRegistra.setOnClickListener{
            inizioRegistrazione()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            //i permessi concessi dovrebbero corrispondere
            // al grantResult (risposta alla richiesta sviluppata dall'utente) che riceviamo di conseguenza
            //è necessario avere un permesso ogni volta che si vuole iniziare a registrare
    }
    // funzione per la gestione del bottone di registrazione, con controllo dei permessi
    private fun inizioRegistrazione() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }
        // start recording
    }
}