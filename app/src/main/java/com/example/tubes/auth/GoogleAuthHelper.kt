package com.example.tubes.auth

import android.app.Activity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.example.tubes.BuildConfig

object GoogleAuthHelper {

    private const val WEB_CLIENT_ID = BuildConfig.GOOGLE_CLIENT_ID

    fun getClient(activity: Activity): GoogleSignInClient {
        Log.d("GoogleAuthHelper", "Web Client ID: ${WEB_CLIENT_ID.take(20)}...")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()

        return GoogleSignIn.getClient(activity, gso)
    }

    suspend fun signInWithGoogle(idToken: String): String {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = FirebaseAuth.getInstance().signInWithCredential(credential).await()
        return result.user?.uid ?: throw Exception("UID null after Google sign-in")
    }
}
