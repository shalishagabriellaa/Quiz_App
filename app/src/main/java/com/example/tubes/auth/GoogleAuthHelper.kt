package com.example.tubes.auth

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object GoogleAuthHelper {

    private const val WEB_CLIENT_ID = "619965562061-h1cn8ipnhv1m09cpfqm3tgemj1ut9l7n.apps.googleusercontent.com"

    fun getClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    suspend fun signInWithGoogle(idToken: String): String {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = FirebaseAuth.getInstance().signInWithCredential(credential).await()
        return result.user?.uid ?: ""
    }
}
