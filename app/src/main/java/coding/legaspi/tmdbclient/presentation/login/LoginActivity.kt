package coding.legaspi.tmdbclient.presentation.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.tmdbclient.BuildConfig
import coding.legaspi.tmdbclient.data.model.auth.LoginBody
import coding.legaspi.tmdbclient.databinding.ActivityLoginBinding
import coding.legaspi.tmdbclient.presentation.di.Injector
import coding.legaspi.tmdbclient.presentation.home.HomeActivity
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.tmdbclient.utils.VibrateView
import javax.inject.Inject
import coding.legaspi.tmdbclient.Result
import coding.legaspi.tmdbclient.data.model.error.Error
import coding.legaspi.tmdbclient.data.model.auth.LoginBodyOutput
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.DialogHelperFactory
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var loginViewModel: EventViewModel
    private lateinit var binding: ActivityLoginBinding

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var sharePreferences: SharedPreferences
    private lateinit var dialogHelper: DialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        loginViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        sharePreferences = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        dialogHelper = DialogHelperFactory.create(this)

        binding.progressBar.visibility = GONE
        binding.loginBtn.setOnClickListener {
            if (validate()){
                binding.progressBar.visibility = VISIBLE
                try {
                    val responseLiveData = loginViewModel.getLoginEventUseCase(LoginBody(email, password))
                    responseLiveData.observe(this, Observer {
                        when(it){
                            is Result.Success<*> -> {
                                val result = it.data as LoginBodyOutput
                                if (result!=null){
                                    Log.d("Login", "Login 0 ${result}")
                                    Log.d("Login", result.toString())
                                    Log.d("Login", "Login 1 $it")
                                    if (result.username.equals("admin")){
                                        Log.d("Login", "Success "+result.token)
                                        binding.progressBar.visibility = GONE
                                        sharePreferences.edit().putString("token", result.token).apply()
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        binding.progressBar.visibility = GONE
                                        Log.d("Login", "Failed "+result.token.toString())
                                    }
                                }else{
                                    binding.progressBar.visibility = GONE
                                    Log.e("Login", "Login 2 $it")
                                    Toast.makeText(this, ""+it, Toast.LENGTH_SHORT).show()
                                }
                            }
                            is Result.Error -> {
                                val exception = it.exception

                                if (exception is IOException) {
                                    binding.progressBar.visibility = GONE
                                    if (exception.localizedMessage!! == "timeout"){
                                        dialogHelper.showUnauthorized(
                                            Error(
                                                "Server error",
                                                "Server is down or not reachable ${exception.message}"
                                            )
                                        )
                                    } else{
                                        dialogHelper.showUnauthorized(Error("Error",exception.localizedMessage!!))
                                    }
                                } else {
                                    binding.progressBar.visibility = GONE
                                    dialogHelper.showUnauthorized(Error("Error","Something went wrong!"))
                                }
                            }
                            Result.Loading -> {
                                binding.progressBar.visibility = VISIBLE
                            }
                        }
                    })
                }catch (e: Exception){
                    binding.progressBar.visibility = GONE
                    Toast.makeText(this, "Check your internet connection...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateEmailAndPass(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var isValid = true
        var errorMessage: String? = null
        password = binding.etPassword.text.toString()
        email = binding.etEmail.text.toString()

        if (password.isEmpty()) {
            errorMessage = "Password is required!"
            isValid = false
        } else if (password.length < 8) {
            errorMessage = "Password must be at least 8 characters long!"
            isValid = false
        }

        if (email.isEmpty()) {
            errorMessage = "Confirm Password is required!"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Invalid Email!"
            isValid = false
        }

        if (isValid && shouldUpdateView) {
            binding.etPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(context, this)
            }
            binding.etEmailTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(context, this)
            }
        }
        return isValid
    }

    private fun validate(): Boolean {
        var isValid = true
        if (!validateEmailAndPass(shouldVibrate = false)) {
            isValid = false
        }

        if (!isValid) {
            VibrateView.vibrate(this, binding.rrlInput)
        }

        return isValid
    }
}