package com.example.localserviceapp.data.repository

import com.example.localserviceapp.data.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun registerUser(user: User, password: String): Flow<Result<AuthResult>> = flow {
        val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
        // Make sure the User object has the UID from Auth
        val userWithId = user.copy(uid = authResult.user!!.uid)
        firestore.collection("users").document(authResult.user!!.uid).set(userWithId).await()
        emit(Result.success(authResult))
    }

    override fun loginUser(email: String, password: String): Flow<Result<User>> = flow {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val userDoc = firestore.collection("users").document(authResult.user!!.uid).get().await()
        val user = userDoc.toObject(User::class.java)
        if (user != null) {
            emit(Result.success(user))
        } else {
            emit(Result.failure(Exception("User data not found in Firestore.")))
        }
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun logoutUser() {
        auth.signOut()
    }

    override fun forgotPassword(email: String): Flow<Result<Unit>> = flow {
        auth.sendPasswordResetEmail(email).await()
        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
    }
}
