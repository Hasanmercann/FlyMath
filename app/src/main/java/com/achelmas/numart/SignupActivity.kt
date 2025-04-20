package com.achelmas.numart

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private var signupButton: CardView? = null

    private var fullnameEditText: TextInputLayout? = null
    private var emailEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null

    private var loadingAnimation: LottieAnimationView? = null
    private var signupTextFromSignupBtn: TextView? = null
    private var loginButton: TextView? = null

    //Firebase
    private var mAuth: FirebaseAuth? = null
    private var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()

        signupButton = findViewById(R.id.signupActivity_signupButtonId)
        signupButton!!.setOnClickListener {
            // Signup Function
            signupProcess()
        }

        loginButton = findViewById(R.id.signupActivity_loginButtonId)
        loginButton!!.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signupProcess() {
        //Initialize the variables
        fullnameEditText = findViewById(R.id.signupActivity_fullnameId)
        emailEditText = findViewById(R.id.signupActivity_emailId)
        passwordEditText = findViewById(R.id.signupActivity_passwordId)
        loadingAnimation = findViewById(R.id.signupActivity_signupButtonId_loadingLottie)
        signupTextFromSignupBtn = findViewById(R.id.signupActivity_signupButtonId_textView)

        var fullname = fullnameEditText!!.editText!!.text.toString().trim()
        var email = emailEditText!!.editText!!.text.toString().trim()
        var password = passwordEditText!!.editText!!.text.toString().trim()

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            if (fullname.isEmpty()) {
                Toast.makeText(this, resources.getString(R.string.please_enter_your_fullname), Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, resources.getString(R.string.please_enter_your_email), Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, resources.getString(R.string.please_enter_your_password), Toast.LENGTH_SHORT).show()
            }
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, resources.getString(R.string.please_enter_a_valid_email), Toast.LENGTH_SHORT).show()
        }
        else if (password.length < 6) {
            Toast.makeText(this, resources.getString(R.string.password_must_be_at_least_6_characters), Toast.LENGTH_SHORT).show()
        }
        else {
            loadingAnimation!!.visibility = View.VISIBLE
            signupTextFromSignupBtn!!.visibility = View.GONE

            // Hide the keyboard
            var inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(signupButton!!.windowToken, 0)

            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this , { task ->
                if (task.isSuccessful()) {
                    var firebaseUser: FirebaseUser? = mAuth!!.currentUser
                    val userId: String = firebaseUser!!.uid

                    // Upload user details
                    uploadUserDetails(userId , fullname , email)
                }
                else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        // email already exists in the database
                        Toast.makeText(this, resources.getString(R.string.email_already_exists), Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                    loadingAnimation!!.visibility = View.GONE
                    signupTextFromSignupBtn!!.visibility = View.VISIBLE
                }
            } )
        }
    }

    private fun uploadUserDetails(userId: String, fullname: String, email: String) {
        reference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        var data:HashMap<String , Any> = HashMap<String, Any>().apply {
            put("id", userId)
            put("fullname", fullname.trim())
            put("email", email)

            // Add nested progress data
            put("UserProgress", hashMapOf(
                "1" to true,
                "2" to false,
                "3" to false,
                "4" to false,
                "5" to false,
                "6" to false,
                "7" to false,
                "8" to false,
                "9" to false,
                "10" to false
            ))
        }

        reference!!.setValue(data).addOnCompleteListener {task ->
            if (task.isSuccessful) {

                Toast.makeText(this, resources.getString(R.string.signup_successful), Toast.LENGTH_SHORT).show()
                loadingAnimation!!.visibility = View.GONE
                signupTextFromSignupBtn!!.visibility = View.VISIBLE

                var intent = Intent(this, LoginActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}