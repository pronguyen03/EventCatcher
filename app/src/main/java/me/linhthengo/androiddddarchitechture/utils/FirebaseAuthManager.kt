package me.linhthengo.androiddddarchitechture.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager(val auth: FirebaseAuth) {

    fun getCurrentUser() = auth.currentUser

    fun getUpdateUser(user: FirebaseUser) = auth.updateCurrentUser(user)

    fun signOut() = auth.signOut()

    fun register(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password)

    fun signInWithEmail(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)

}
