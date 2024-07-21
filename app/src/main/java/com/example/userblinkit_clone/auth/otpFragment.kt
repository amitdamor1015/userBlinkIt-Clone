package com.example.userblinkit_clone.auth

import SmsBroadcastReceiver
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkit_clone.R
import com.example.userblinkit_clone.Utils
import com.example.userblinkit_clone.activity.UserActivity
import com.example.userblinkit_clone.databinding.FragmentOtpBinding
import com.example.userblinkit_clone.models.Users
import com.example.userblinkit_clone.viewModels.authViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import kotlinx.coroutines.launch

class otpFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding
    private val _otpModel: authViewModel by viewModels()
    private lateinit var smsRetrieverClient: SmsRetrieverClient
    private val smsBroadcastReceiver = SmsBroadcastReceiver { otp ->
        otp?.let {
            fillOtp(otp)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        otpTextWatcher()

        val bundle = arguments
        val userNumber = bundle?.getString("mobile_Num").toString()
        binding.tvOtpText.text = "OTP will be sent to the number below \n+91 $userNumber"

        sendOtp(userNumber)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_otpFragment_to_signInFragment)
        }

        binding.btnOtpverify.setOnClickListener {
            Utils.showDialog(requireContext(), "Verifying OTP")

            val otpDigits = arrayOf(
                binding.etOtp1, binding.etOtp2, binding.etOtp3,
                binding.etOtp4, binding.etOtp5, binding.etOtp6
            )
            val otp = otpDigits.joinToString("") { it.text.toString() }

            if (otp.length != otpDigits.size) {
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Enter a valid OTP")
            } else {
                otpDigits.forEach { it.text?.clear(); it.clearFocus() }
                verifyOtp(otp, userNumber)
            }
        }

        // Initialize SMS Retriever
        smsRetrieverClient = SmsRetriever.getClient(requireActivity())
        startSmsRetriever()

        return binding.root
    }

    private fun startSmsRetriever() {
        smsRetrieverClient.startSmsRetriever()
            .addOnSuccessListener {
                // Successfully started SMS Retriever
            }
            .addOnFailureListener {
                // Failed to start SMS Retriever
            }
    }

    private fun verifyOtp(otp: String, userNumber: String) {
        val users = Users(uid = Utils.getCurrentUserId(), userPhoneNumber = userNumber, userAddress = null)

        _otpModel.signInWithPhoneAuthCredential(otp, userNumber, users)
        lifecycleScope.launch {
            _otpModel.isSignInSuccessful.collect {
                if (it) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Login Successful")

                    startActivity(Intent(requireActivity(), UserActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sendOtp(userNumber: String) {
        Utils.showDialog(requireContext(), "Sending OTP")
        _otpModel.sendOtp(userNumber, requireActivity())
        lifecycleScope.launch {
            _otpModel.otpSent.collect { isSent ->
                if (isSent) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "OTP Sent")
                }
            }
        }
    }

    private fun otpTextWatcher() {
        val otpDigits = arrayOf(
            binding.etOtp1,
            binding.etOtp2,
            binding.etOtp3,
            binding.etOtp4,
            binding.etOtp5,
            binding.etOtp6
        )

        for (i in otpDigits.indices) {
            otpDigits[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // No implementation needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < otpDigits.size - 1) {
                        otpDigits[i + 1].requestFocus()
                    } else if (s?.length == 0 && i > 0) {
                        otpDigits[i - 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // No implementation needed
                }
            })

            otpDigits[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && otpDigits[i].text.isNullOrEmpty() && i > 0) {
                    otpDigits[i - 1].requestFocus()
                }
                false
            }
        }
    }

    private fun fillOtp(otp: String) {
        val otpDigits = arrayOf(
            binding.etOtp1, binding.etOtp2, binding.etOtp3,
            binding.etOtp4, binding.etOtp5, binding.etOtp6
        )
        otpDigits.forEachIndexed { index, editText ->
            editText.setText(otp[index].toString())
        }
        binding.btnOtpverify.performClick() // Trigger OTP verification
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(smsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(smsBroadcastReceiver)
    }
}
