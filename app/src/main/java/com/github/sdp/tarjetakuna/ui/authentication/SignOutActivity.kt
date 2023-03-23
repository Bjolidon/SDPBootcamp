package com.github.sdp.tarjetakuna.ui.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.sdp.tarjetakuna.MainActivity
import com.github.sdp.tarjetakuna.R
import com.github.sdp.tarjetakuna.database.UserCardsActivity
import com.github.sdp.tarjetakuna.ui.collectionexport.ExportCollectionActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * This activity is used to display a button to sign out the user.
 */
class SignOutActivity: AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_out)

        val user = Firebase.auth.currentUser
        val newMessage = user?.displayName
        val greetingMessage: TextView = findViewById(R.id.greetingMessage)
        greetingMessage.text = "Hello $newMessage!"

        val signOutButton = findViewById<TextView>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            val intent = Intent(this, AuthenticationActivity::class.java)
            intent.putExtra("signIn", false)
            startActivity(intent)
        }
        val backButton = findViewById<Button>(R.id.button_back_home)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val configureCollectionButton  = findViewById<Button>(R.id.manage_collection_button)
        configureCollectionButton.setOnClickListener {
            val intent = Intent(this, UserCardsActivity::class.java)
            startActivity(intent)
        }

        val exportCollectionButton  = findViewById<Button>(R.id.export_collection_button)
        exportCollectionButton.setOnClickListener {
            val intent = Intent(this, ExportCollectionActivity::class.java)
            startActivity(intent)
        }
    }
}
