package com.example.cognitointegration.ui

import android.R.attr.password
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.example.cognitointegration.R


class MainActivity : AppCompatActivity() {

    private lateinit var registerBtn:Button
    private lateinit var loginBtn:Button
    private lateinit var tvName:EditText
    private lateinit var tvEmail:EditText
    private lateinit var tvMobile:EditText
    private lateinit var tvUserName:EditText
    private lateinit var tvPassword:EditText
    private lateinit var tvLoginUserName:EditText
    private lateinit var tvLoginPassword:EditText

    private val cognitoUserAttributes = CognitoUserAttributes()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        registerBtn.setOnClickListener {
            cognitoUserAttributes.addAttribute("given_name",tvName.text.toString())
            cognitoUserAttributes.addAttribute("phone_number",tvMobile.text.toString())
            cognitoUserAttributes.addAttribute("email",tvEmail.text.toString())

            // setup AWS service configuration. Choosing default configuration
            // setup AWS service configuration. Choosing default configuration
            val clientConfiguration = ClientConfiguration()

            // Create a CognitoUserPool object to refer to your user pool
            // Create a CognitoUserPool object to refer to your user pool
            val userPool =
                CognitoUserPool(this, resources.getString(R.string.user_pool_id), resources.getString(
                    R.string.client_id
                ), resources.getString(R.string.client_secret), clientConfiguration)

            userPool.signUpInBackground(tvUserName.text.toString(),tvPassword.text.toString(),cognitoUserAttributes,null,signupCallback)
        }

        loginBtn.setOnClickListener{
            // setup AWS service configuration. Choosing default configuration
            // setup AWS service configuration. Choosing default configuration
            val clientConfiguration = ClientConfiguration()

            // Create a CognitoUserPool object to refer to your user pool
            // Create a CognitoUserPool object to refer to your user pool
            val userPool =
                CognitoUserPool(this, resources.getString(R.string.user_pool_id), resources.getString(
                    R.string.client_id
                ), resources.getString(R.string.client_secret), clientConfiguration)
            val user = userPool.getUser(tvLoginUserName.text.toString())
            user.getSessionInBackground(authenticationHandler)
        }
    }

    fun init(){
        registerBtn =  findViewById(R.id.btn_register)
        tvName = findViewById(R.id.name)
        tvEmail = findViewById(R.id.email)
        tvMobile = findViewById(R.id.mobile)
        tvUserName = findViewById(R.id.user_name)
        tvPassword = findViewById(R.id.password)
        tvLoginUserName = findViewById(R.id.login_username)
        tvLoginPassword = findViewById(R.id.user_password)
        loginBtn = findViewById(R.id.btn_login)
    }

    val signupCallback: SignUpHandler = object : SignUpHandler {
        override fun onSuccess(
            cognitoUser: CognitoUser,
            userConfirmed: Boolean,
            cognitoUserCodeDeliveryDetails: CognitoUserCodeDeliveryDetails
        ) {
            // Sign-up was successful
            Log.e("onSuccess","Sign-up was successful")
            // Check if this user (cognitoUser) has to be confirmed
            if (!userConfirmed) {
                // This user has to be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
                Log.e("userConfirmed",cognitoUser.userId)
                Log.e("code if",cognitoUserCodeDeliveryDetails.toString())
            } else {
                // The user has already been confirmed
                Log.e("succ else","Sign-up was successful")
            }
        }

        override fun onFailure(exception: java.lang.Exception) {
            // Sign-up failed, check exception for the cause
            Log.e("onFailure",exception.toString())
        }
    }

    // Callback handler for the sign-in process
    var authenticationHandler: AuthenticationHandler = object : AuthenticationHandler {

        override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
            Log.e("onSuccess username",userSession?.username.toString())
            Log.e("onSuccess accessToken",userSession?.accessToken?.jwtToken.toString())
            Log.e("newDevice",newDevice.toString())
        }

        override fun getAuthenticationDetails(
            authenticationContinuation: AuthenticationContinuation,
            userId: String
        ) {
            // The API needs user sign-in credentials to continue
            val authenticationDetails = AuthenticationDetails(userId, tvLoginPassword.text.toString(), null)

            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails)

            // Allow the sign-in to continue
            authenticationContinuation.continueTask()
        }

        override fun getMFACode(multiFactorAuthenticationContinuation: MultiFactorAuthenticationContinuation) {
            // Multi-factor authentication is required, get the verification code from user
           // multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode)
            // Allow the sign-in process to continue
//            multiFactorAuthenticationContinuation.continueTask()
            Log.e("getMFACode",multiFactorAuthenticationContinuation.toString())
        }

        override fun authenticationChallenge(continuation: ChallengeContinuation?) {
            TODO("Not yet implemented")
        }

        override fun onFailure(exception: Exception) {
            Log.e("onFailure",exception.toString())
            // Sign-in failed, check exception for the cause
        }
    }

}