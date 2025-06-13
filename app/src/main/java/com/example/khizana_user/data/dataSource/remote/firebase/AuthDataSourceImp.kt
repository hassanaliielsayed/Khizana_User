package com.example.khizana_user.data.dataSource.remote.firebase

import android.content.Context
import android.util.Log
import com.example.khizana_user.data.repository.AuthDataSource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthDataSource {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, password: String): Result<Unit> = try {
        auth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun register(email: String, password: String): Result<Unit> = try {
        auth.createUserWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        auth.signOut()

        val googleSignInClient = GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("864102683705-a6u9dmg1vb8ep00be4ndvda2c9n57obt.apps.googleusercontent.com")
                .requestEmail()
                .build()
        )

        googleSignInClient.signOut().await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun loginAsGuest(): Result<Unit> = try {
        FirebaseAuth.getInstance().signInAnonymously().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun isAnonymousUser(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.isAnonymous == true
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        val user = auth.currentUser
        return if (user != null) {
            try {
                user.sendEmailVerification().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("No user logged in"))
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        val user = auth.currentUser
        user?.reload()?.await()
        return user?.isEmailVerified == true
    }

    override fun getCurrentUserEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }

    override fun getCurrentUserName(): String? {
        return FirebaseAuth.getInstance().currentUser?.displayName
    }
}
