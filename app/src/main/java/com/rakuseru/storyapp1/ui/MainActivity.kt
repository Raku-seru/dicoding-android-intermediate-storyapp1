package com.rakuseru.storyapp1.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.faltenreich.skeletonlayout.createSkeleton
import com.rakuseru.storyapp1.R
import com.rakuseru.storyapp1.data.RequestLogin
import com.rakuseru.storyapp1.databinding.ActivityMainBinding
import com.rakuseru.storyapp1.preference.UserPreference
import com.rakuseru.storyapp1.ui.viewmodel.UserViewModel
import com.rakuseru.storyapp1.ui.viewmodel.MainViewModel
import com.rakuseru.storyapp1.ui.viewmodel.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var skEmail: Skeleton
    private lateinit var skPass: Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // User Session Data
        val pref = UserPreference.getInstance(dataStore)
        val userViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]

        // Check user login state
        userViewModel.getLoginState().observe(this) { state ->
            if (state) {
                showLoading(false)
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        mainViewModel.message.observe(this) {
            val usr = mainViewModel.userLogin.value
            checkResponse(it, usr?.loginResult?.token, mainViewModel.isError, userViewModel)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        // Init activity functions
        setAction()

        skEmail = binding.tiEmail.createSkeleton()
        skPass = binding.tiPass.createSkeleton()
    }

    // Custom View Actions
    private fun setAction() {
        // Login Button
        binding.btnLogin.setOnClickListener {
            binding.tiEmail.clearFocus()
            binding.tiPass.clearFocus()

            if (isDataValid()) {
                val user = RequestLogin(
                    binding.tiEmail.text.toString().trim(),
                    binding.tiPass.text.toString().trim()
                )
                mainViewModel.getResponseLogin(user)
            }

        }

        // SignUp button
        binding.signUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // See password checkbox
        binding.seePassword.setOnClickListener {
            if (binding.seePassword.isChecked) {
                binding.tiPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.tiPass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

    }

    // Skeleton loading
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            skEmail.showSkeleton()
            skPass.showSkeleton()
        } else {
            skEmail.showOriginal()
            skPass.showOriginal()
        }
    }

    // Check whether data is valid
    private fun isDataValid(): Boolean {
        return binding.tiEmail.isEmailValid && binding.tiPass.isPassValid
    }

    // Check Response Login
    private fun checkResponse(
        msg: String,
        token: String?,
        isError: Boolean,
        model: UserViewModel
    ) {
        if (!isError) {
            Toast.makeText(this, "${getString(R.string.success_login)} $msg", Toast.LENGTH_LONG).show()
            model.saveLoginState(true)
            if (token != null) model.saveToken(token)
            model.saveName(mainViewModel.userLogin.value?.loginResult?.name.toString())
        } else {
            when (msg) {
                "Unauthorized" -> {
                    Toast.makeText(this, getString(R.string.unauthorized), Toast.LENGTH_SHORT).show()
                    binding.tiEmail.apply {
                        requestFocus()
                    }
                    binding.tiPass.setText("")
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
}