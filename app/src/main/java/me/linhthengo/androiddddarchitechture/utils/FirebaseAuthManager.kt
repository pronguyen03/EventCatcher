package me.linhthengo.androiddddarchitechture.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager(val auth: FirebaseAuth) {

    //TODO Don't create an instance of auth, should move it to Dagger
    fun getCurrentUser() = auth.currentUser

    fun getUpdateUser(user: FirebaseUser) = auth.updateCurrentUser(user)

    fun signOut() = auth.signOut()

    fun register(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password)

    fun signInWithEmail(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)

}
