package com.example.userblinkit_clone.viewModels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.userblinkit_clone.Utils
import com.example.userblinkit_clone.models.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class authViewModel : ViewModel() {

    private val _verificationID = MutableStateFlow<String?>(null)
    val otpSent = MutableStateFlow(false)

    private val _isSignInSuccessful = MutableStateFlow(false)
    val isSignInSuccessful = _isSignInSuccessful

    private val _isCurrentUser = MutableStateFlow(false)
    val isCurrentUser = _isCurrentUser

    init {
        Utils.getAuth().currentUser?.let {
            _isCurrentUser.value = true
        }
    }

    fun sendOtp(userNumber: String, activity: Activity) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Handle verification completed
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Handle verification failed
                otpSent.value = false
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                _verificationID.value = verificationId
                otpSent.value = true
            }
        }

        val options = PhoneAuthOptions.newBuilder(Utils.getAuth())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp: String, userNumber: String, users: Users) {
        val credential = PhoneAuthProvider.getCredential(_verificationID.value.toString(), otp)
        Utils.getAuth().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isSignInSuccessful.value=true
                    FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(users.uid!!).setValue(users)
                } else {

                }
            }
    }
}
