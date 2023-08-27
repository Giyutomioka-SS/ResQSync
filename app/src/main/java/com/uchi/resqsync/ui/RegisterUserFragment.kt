package com.uchi.resqsync.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.uchi.resqsync.R
import com.uchi.resqsync.utils.Utility
import com.uchi.resqsync.utils.Utility.makeLinks
import com.uchi.resqsync.utils.api.BackendService
import com.uchi.resqsync.utils.api.RegistrationRequest
import com.uchi.resqsync.utils.api.RegistrationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class RegisterUserFragment : Fragment() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var cnfPasswordInput: TextInputEditText
    private lateinit var signUpBtn: MaterialButton
    private lateinit var existingUserText: TextView
    private lateinit var tcCheckBox: CheckBox
    private lateinit var email:String
    private lateinit var password: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailInput = view.findViewById(R.id.user_email)
        passwordInput = view.findViewById(R.id.user_password)
        cnfPasswordInput = view.findViewById(R.id.cnf_user_password)
        signUpBtn = view.findViewById(R.id.signup_button)
        existingUserText = view.findViewById(R.id.existing_user_text)
        tcCheckBox = view.findViewById(R.id.terms_condition_checkbox)

        existingUserText.highlightColor = Color.TRANSPARENT;
        tcCheckBox.highlightColor = Color.TRANSPARENT;
        signUpBtn.isEnabled = false

        watchInputFieldsAndCheckBox()
        privacyPolicyWeb()
        signUpBtn.setOnClickListener {
            initiateSignUp()
        }
        existingUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_user, container, false)
    }

    private fun existingUser() {
        existingUserText.makeLinks(
            Pair("Signin", View.OnClickListener {
                Timber.w("Signin text pressed")
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentContainer, SigninFragment())
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            })
        )
    }

    // TODO redirect the user to the web on the link click
    private fun privacyPolicyWeb() {
        tcCheckBox.makeLinks(
            Pair("Privacy", View.OnClickListener {
                Timber.w("Privacy text pressed")
            }),
            Pair("Terms of Use", View.OnClickListener {
                Timber.w("Terms of Use pressed")
            })
        )
    }

    private fun watchInputFieldsAndCheckBox() {
        tcCheckBox.setOnCheckedChangeListener { _, _ ->
            updateSignUpButtonState()
        }
        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = emailInput.text.toString()
                if (text.isNotEmpty()) {
                    verifyEmail(text)
                }
            }
        }
        passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val password = passwordInput.text.toString()
                val cnfPassword = cnfPasswordInput.text.toString()
                if (password.isNotEmpty() && cnfPassword.isNotEmpty()) {
                    verifyPassword(password,cnfPassword)
                }
            }
        }
    }

    private fun initiateSignUp() {
        Timber.d("Signup button pressed")
        val signUp = BackendService.backendInstance.registerUser(RegistrationRequest(email,password))
        signUp.enqueue(object : Callback<RegistrationResponse>{
            override fun onResponse(
                call: Call<RegistrationResponse>,
                response: Response<RegistrationResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val responseBodyString = Gson().toJson(responseBody)
                        Log.e("TAG", "Response Body: $responseBodyString")
                        // Now you can parse and inspect the response body
                    } else {
                        Log.e("TAG", "Response body is null")
                    }
                } else {
                    Log.e("TAG", "Unsuccessful response: ${response.code()}")
                }
                Toast.makeText(context, "Success$response", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                Log.e("TAG", "API call failed: ${t.message}")
                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyEmailPassword(email:String,password:String,cnfPassword:String):Boolean {
        val emailLayout = view?.findViewById<TextInputLayout>(R.id.email_input_text_layout)
        val passwordLayout = view?.findViewById<TextInputLayout>(R.id.password_input_text_layout)
        if(!Utility.isEmailValid(email)){
            emailLayout?.error = getString(R.string.invalid_email)
            return false
        }else {
            emailLayout?.isErrorEnabled = false
            this.email=emailInput.text.toString()
        }
        if(!Utility.isValidPassword(password)){
            passwordLayout?.error = getString(R.string.invalid_password_format)
            return false
        } else {
            passwordLayout?.isErrorEnabled = false
            this.password = passwordInput.text.toString()
        }
        if(password!=cnfPassword){
            passwordLayout?.error = getString(R.string.password_mismatch)
            return false
        } else passwordLayout?.isErrorEnabled = false
        return true
    }

    private fun updateSignUpButtonState() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val cnfPassword = cnfPasswordInput.text.toString()
        val isTcChecked = tcCheckBox.isChecked
        val isInputValid = verifyEmailPassword(email, password, cnfPassword)
        signUpBtn.isEnabled = isInputValid && isTcChecked
    }

    private fun verifyEmail(email:String):Boolean {
        val emailLayout = view?.findViewById<TextInputLayout>(R.id.email_input_text_layout)
        if(!Utility.isEmailValid(email)){
            emailLayout?.error = getString(R.string.invalid_email)
            return false
        }else {
            emailLayout?.isErrorEnabled = false
            this.email=emailInput.text.toString()
        }
        return true
    }

    private fun verifyPassword(password:String,cnfPassword:String):Boolean {
        val passwordLayout = view?.findViewById<TextInputLayout>(R.id.password_input_text_layout)
        if(!Utility.isValidPassword(password)){
            passwordLayout?.error = getString(R.string.invalid_password_format)
            return false
        } else {
            passwordLayout?.isErrorEnabled = false
            this.password = passwordInput.text.toString()
        }
        if(password!=cnfPassword){
            passwordLayout?.error = getString(R.string.password_mismatch)
            return false
        } else passwordLayout?.isErrorEnabled = false
        return true
    }
}