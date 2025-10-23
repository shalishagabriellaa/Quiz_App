package com.example.tubes

import android.os.Bundle
import android.util.Log // Alam -> Untuk Logging
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tubes.ui.theme.TubesTheme
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // Ini jadi ganti ke jetpack compose?
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore // Alam -> Untuk konek database

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { result ->
//                if (result.isEmpty) {
//                    Toast.makeText(this, "No users found in Firestore", Toast.LENGTH_SHORT).show()
//                    Log.d("Firestore", "No users found.")
//                } else {
//                    for (document in result) {
//                        val name = document.getString("full_name")
//                        val email = document.getString("email")
//                        Log.d("Firestore", "User: $name, Email: $email")
//                    }
//                    Toast.makeText(this, "Firestore Loaded!", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                Log.e("Firestore", "Error getting documents", e)
//            }
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TubesTheme {
        Greeting("Android")
    }
}