package com.example.cognitointegration.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.example.cognitointegration.R


class MainActivity : AppCompatActivity() {

    val userPool: CognitoUserPool? = null

    private lateinit var registerBtn:Button
    private lateinit var tvName:EditText
    private lateinit var tvEmail:EditText
    private lateinit var tvMobile:EditText
    private lateinit var tvUserName:EditText
    private lateinit var tvPassword:EditText

    val cognitoUserAttributes = CognitoUserAttributes()

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
    }

    fun init(){
        registerBtn =  findViewById(R.id.btn_register)
        tvName = findViewById(R.id.name)
        tvEmail = findViewById(R.id.email)
        tvMobile = findViewById(R.id.mobile)
        tvUserName = findViewById(R.id.user_name)
        tvPassword = findViewById(R.id.password)
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
}