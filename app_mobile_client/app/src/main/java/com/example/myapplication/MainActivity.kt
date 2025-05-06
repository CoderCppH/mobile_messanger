package com.example.myapplication

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ACTIVITY.MainMenuActivity
import com.example.myapplication.API.HttpClient
import com.example.myapplication.CONFIG_USER.ConfigUser
import com.example.myapplication.CONFIG_USER.Person
import com.example.myapplication.GL.GL
import com.example.myapplication.MAILSS.MailSs
import com.example.myapplication.SETUP.SetUp
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var img: ImageView
    private lateinit var et_email: EditText
    private lateinit var g_code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        SetUp(this)
        img = findViewById(R.id.img_V)
        loopRotationImg()
        et_email = findViewById(R.id.activity_main_edit_text_email)
        val user = ConfigUser (this).get_user()
        val gson = Gson()
        Log.d("Config.User", gson.toJson(user))
        if (user.id >= 0) {
            nextActivity()
        }
    }

    private fun loopRotationImg() {
        val animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                img.rotationY = value
                img.rotationX = value
            }
        }
        animator.start()
    }

    private fun generatorCode(): String {
        val alpha = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890"
        return (1..6).map { alpha[Random.nextInt(alpha.length)] }.joinToString("")
    }

    private fun checkGmailIndex(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun nextActivity() {
        finishAffinity()
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    public fun onClickGoTo(view: View) {
        val emailString = et_email.text.toString()
        if (checkGmailIndex(emailString)) {
            CoroutineScope(Dispatchers.IO).launch {
                val emailSender = MailSs()
                g_code = generatorCode()
                val bodyText = "только ни кому не сообщай об этом коде !!! \n code: $g_code"
                val headText = "очень важный код от ChatLink"

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, emailString, Toast.LENGTH_SHORT).show()
                }

                emailSender.sendMessage(bodyText, headText, emailString)
                showCodeDialog(emailString)
            }
        } else {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCodeDialog(emailString: String) {
        // Переключаемся на основной поток для создания диалога
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@MainActivity)
            dialog.setTitle("CODE")
            dialog.setContentView(R.layout.dialog_activity)

            val codeEditText: EditText = dialog.findViewById(R.id.dialog_edit_text_code)
            val confirmButton: Button = dialog.findViewById(R.id.dialog_confirm_code_btn)

            confirmButton.setOnClickListener {
                val inputCode = codeEditText.text.toString()
                if (inputCode == g_code) {
                    Log.d("input_code", "SUCCESS")
                    val pUser  = Person().apply {
                        email = emailString
                        first_name = "null"
                        last_name = "null"
                        id = -1
                    }

                    val configUser  = ConfigUser (this@MainActivity)
                    val api = HttpClient()
                    val gson = Gson()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val res = api.POST(GL.url_api_server + "users", gson.toJson(pUser ))
                            Log.d("API.POST.users", res)
                            Log.d("Config.User", gson.toJson(pUser ))

                            if (res.trim() == "{\"message\": \"success inserts user\"}") {
                                configUser .edit_config_user(pUser )
                                withContext(Dispatchers.Main) {
                                    nextActivity()
                                }
                                Log.d("REGISTER", "Success")
                            } else {
                                configUser .edit_config_user(pUser )
                                val loginRes = api.POST(GL.url_api_server + "find_user", gson.toJson(pUser ))
                                Log.d("LOGIN", "INIT")
                                Log.d("API.POST.LOGIN_USER", loginRes)
                                val person = gson.fromJson(loginRes, Person::class.java)
                                withContext(Dispatchers.Main) {
                                    if (person.id > 0) {
                                        configUser .edit_config_user(person)
                                        nextActivity()
                                        Log.d("LOGIN", "Success")
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "хмм где-то ошибка: $loginRes",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Произошла ошибка: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            Log.e("API.Error", e.toString())
                        }
                    }
                    dialog.dismiss()
                } else {
                    Toast.makeText(this@MainActivity, "Неверный код", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }
    }
}