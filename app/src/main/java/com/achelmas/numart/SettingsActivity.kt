package com.achelmas.numart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var alertDialogBuilder : AlertDialog.Builder
    private lateinit var logOutBtn: CardView
    private lateinit var appLanguageBtn: CardView

    private var fullnameTextView: TextView? = null
    private lateinit var fullname: String
    private var mAuth: FirebaseAuth? = null

    private var targetsUnlockedTextView: TextView? = null

    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mAuth = FirebaseAuth.getInstance()
        // Initialize user reference
        userRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(mAuth!!.currentUser!!.uid)

        toolbar = findViewById(R.id.settingsActivity_toolBarId)
        // Set arrow back button to Toolbar
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        fullnameTextView = findViewById(R.id.settingsActivity_fullnameId)
        targetsUnlockedTextView = findViewById(R.id.settingsActivity_targetsUnlockedId) // Yeni TextView

        var bundle: Bundle = intent.extras!!
        if(bundle != null) {
            fullname = bundle.getString("fullname")!!.toString()
        }
        fullnameTextView!!.text = fullname

        getTargetsUnlocked()


        appLanguageProcess()
        logOutProcess()

    }

    private fun appLanguageProcess() {
        appLanguageBtn = findViewById(R.id.settingsActivity_appLanguageBtnId)
        appLanguageBtn.setOnClickListener {
            var bottomSheetView: View = LayoutInflater.from(this).inflate( R.layout.bottom_sheet_layout_of_languages ,
                findViewById(R.id.rootBottomSheet) )

            var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

            val englishBtn: RelativeLayout = bottomSheetView.findViewById(R.id.rLEnglish)
            val turkishBtn: RelativeLayout = bottomSheetView.findViewById(R.id.rLTurkish)
            val englishTxtV: TextView = bottomSheetView.findViewById(R.id.tVEnglish)
            val turkishTxtV: TextView = bottomSheetView.findViewById(R.id.tVTurkish)

            val languageBtns = listOf(
                englishBtn, turkishBtn
            )

            // Set current language selection
            var currentLanguage = LanguageManager.loadSelectedLanguage(this)
            when (currentLanguage) {
                "en" -> {
                    englishBtn.background = getDrawable(R.drawable.selected_language_btn)
                    englishTxtV.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))
                }
                "tr" -> {
                    turkishBtn.background = getDrawable(R.drawable.selected_language_btn)
                    turkishTxtV.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))
                }
            }

            // LinearLayout listeners
            englishBtn.setOnClickListener {
                if (currentLanguage != "en") {
                    uncheckPreviousBtn(languageBtns, englishBtn)
                    englishBtn.background = getDrawable(R.drawable.selected_language_btn)
                    englishTxtV.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))
                    currentLanguage = "en"
                    saveAndChangeLanguage("en")
                }
            }

            turkishBtn.setOnClickListener {
                if (currentLanguage != "tr") {
                    uncheckPreviousBtn(languageBtns, turkishBtn)
                    turkishBtn.background = getDrawable(R.drawable.selected_language_btn)
                    turkishTxtV.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))
                    currentLanguage = "tr"
                    saveAndChangeLanguage("tr")
                }
            }
        }
    }

    private fun uncheckPreviousBtn(languageBtns: List<RelativeLayout>, selectedBtn: RelativeLayout) {
        languageBtns.forEach { lB ->
            if (lB != selectedBtn) {
                lB.background = getDrawable(R.drawable.language_btn)
            }
        }
    }

    // Save language and refresh UI
    private fun saveAndChangeLanguage(language: String) {
        LanguageManager.setLocaleLanguage(this, language)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun getTargetsUnlocked() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userProgressRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId!!)
            .child("UserProgress")

        userProgressRef.get().addOnSuccessListener { snapshot ->
            var unlockedTargets = 0
            snapshot.children.forEach { child ->
                if (child.value == true) {
                    unlockedTargets++
                }
            }

            // Hedeflerin sayısını TextView'de göster
            targetsUnlockedTextView?.text = "$unlockedTargets"
        }.addOnFailureListener {
            Toast.makeText(this, resources.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        }
    }

    private fun logOutProcess() {
        // Initialize the variables
        logOutBtn = findViewById(R.id.settingsActivity_logOutBtnId)
        logOutBtn.setOnClickListener {

            alertDialogBuilder  = AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(resources.getString(R.string.log_out))
                .setMessage(resources.getString(R.string.log_out_message))
                .setPositiveButton(resources.getString(R.string.log_out)) { dialogInterface, i ->

                    mAuth!!.signOut()

                    var intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, resources.getString(R.string.log_out_success), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { dialogInterface, i ->
                    dialogInterface.cancel()
                }

            var alertDialog : AlertDialog = alertDialogBuilder.create()

            //Change The Color of LOG OUT Btn
            alertDialog.setOnShowListener {
                var positiveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))

                var negativeButton: Button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setTextColor(Color.parseColor("#F0F3F8"))
            }
            alertDialog.show()

            // for when the user log out he can again signup but he can't enter to mainAc until login
            clearLoginFlag()
        }
    }

    private fun clearLoginFlag() {
        val prefs: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.remove("loginCompleted")
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        // Set Language
        LanguageManager.loadLocale(this)
    }
}