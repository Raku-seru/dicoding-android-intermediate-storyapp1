package com.rakuseru.storyapp1.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.rakuseru.storyapp1.R
import com.rakuseru.storyapp1.data.RequestLogin
import com.rakuseru.storyapp1.data.RequestRegister
import com.rakuseru.storyapp1.databinding.ActivityRegisterBinding
import com.rakuseru.storyapp1.preference.UserPreference
import com.rakuseru.storyapp1.ui.viewmodel.MainViewModel
import com.rakuseru.storyapp1.ui.viewmodel.RegisterViewModel
import com.rakuseru.storyapp1.ui.viewmodel.UserViewModel
import com.rakuseru.storyapp1.ui.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    //ViewModels
    private val registerViewModel: RegisterViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    // var for contain user data from register to login
    private var isPwdMatch: Boolean = false
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var pass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setAction()

        // User Session Data
        val pref = UserPreference.getInstance(dataStore)
        val userViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]

        // Check whether the user has login state
        userViewModel.getLoginState().observe(this) { state ->
            if (state) {
                val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // clear token
                userViewModel.saveToken("")
                userViewModel.saveName("")
            }
        }

        registerViewModel.message.observe(this) {
            checkResponseeRegister(it, registerViewModel.isError)
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.userLogin.observe(this) {
            userViewModel.saveLoginState(true)
            userViewModel.saveToken(it.loginResult.token)
            userViewModel.saveName(it.loginResult.name)

        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    // Custom View Actions
    private fun setAction() {
        // Input Password
        binding.tiPass.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    isPasswordMatch()
                }
            }
        }
        binding.tiCpass.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    isPasswordMatch()
                }
            }
        }

        // Button Register
        binding.btnRegister.setOnClickListener {
            binding.apply {
                tiEmail.clearFocus()
                tiName.clearFocus()
                tiPass.clearFocus()
                tiCpass.clearFocus()
            }

            if (isDataValid()) {
                name = binding.tiName.text.toString().trim()
                email = binding.tiEmail.text.toString().trim()
                pass = binding.tiPass.text.toString().trim()

                val user = RequestRegister(
                    name,
                    email,
                    pass
                )

                registerViewModel.getResponseRegister(user)
            }
        }

        // See password check box
        binding.seePassword.setOnClickListener {
            if (binding.seePassword.isChecked) {
                binding.tiPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.tiCpass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.tiPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.tiCpass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun checkResponseeRegister(msg: String, isError: Boolean) {
        if (!isError) {
            Toast.makeText(this, getString(R.string.user_created), Toast.LENGTH_SHORT).show()
            val user = RequestLogin(
                email,
                pass
            )
            mainViewModel.getResponseLogin(user)
        } else {
            when (msg) {
                "Bad Request" -> {
                    Toast.makeText(this, getString(R.string.email_taken), Toast.LENGTH_SHORT).show()
                    binding.tiEmail.apply {
                        setText("")
                        requestFocus()
                    }
                }
                "timeout" -> {
                    Toast.makeText(this, getString(R.string.timeout), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "${getString(R.string.error_message)} $msg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Check whether data is valid and input it to lateinit var
    private fun isDataValid(): Boolean {
        return binding.tiName.isNameValid &&
                binding.tiEmail.isEmailValid &&
                binding.tiPass.isPassValid &&
                binding.tiCpass.isCPassValid &&
                isPwdMatch
    }

    private fun isPasswordMatch() {
        if (binding.tiPass.text.toString().trim() != binding.tiCpass.text.toString().trim()) {
            binding.tiCpass.error = resources.getString(R.string.pass_not_match)
            isPwdMatch = false
        } else {
            binding.tiCpass.error = null
            isPwdMatch = true
        }
    }

    // Skeleton Loading
    private fun showLoading(isLoading: Boolean) {
        val skeleton = findViewById<SkeletonLayout>(R.id.skeletonLayout)
        if (isLoading) skeleton.showSkeleton() else skeleton.showOriginal()
    }

}