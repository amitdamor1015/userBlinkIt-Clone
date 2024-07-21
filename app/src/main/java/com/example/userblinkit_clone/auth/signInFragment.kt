package com.example.userblinkit_clone.auth

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.userblinkit_clone.R
import com.example.userblinkit_clone.Utils
import com.example.userblinkit_clone.databinding.FragmentSignInBinding

class signInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSignInBinding.inflate(layoutInflater)
        setStatusBarColor()


        binding.btnSendCode.setOnClickListener(View.OnClickListener {
            sendCode()
        })
        return this.binding.root
    }

    private fun sendCode() {
        val mobileNum=binding.etMobileNum.text.toString()

        if(mobileNum.length!=10 || mobileNum.isEmpty()){
            binding.etMobileNum.error="Enter mobile number"
            Utils.showToast(requireContext(), "Enter mobile number")
        }else{
            val bundle=Bundle()
            bundle.putString("mobile_Num", mobileNum)
            findNavController().navigate(R.id.action_signInFragment_to_otpFragment, bundle)
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun setStatusBarColor() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.yellow)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}