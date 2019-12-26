package com.example.imdb.login

import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imdb.ErrorModel
import com.example.imdb.NetworkService
import com.example.imdb.NetworkServiceImpl
import com.example.imdb.R
import com.example.imdb.extension.closeKeyboard
import com.example.imdb.extension.setOnDrawableEndClick
import com.example.imdb.extension.showMessage
import com.example.imdb.model.account.AccountDetailModel
import com.example.imdb.model.login.SessionModel
import com.example.imdb.model.login.TokenModel
import com.example.imdb.model.login.UserRequestModel
import com.example.imdb.model.login.UserResponseModel
import com.example.imdb.profile.ProfileFragment
import com.example.imdb.series.MainContainerFragment
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    val networkService: NetworkService by lazy { NetworkServiceImpl() }
    var userRequestModel = UserRequestModel(null, null, null)
    var tokenModel = TokenModel(null)
    var email: String? = null
    var password: String? = null

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }

        const val SESSION_ID = "sessionId"
        const val ACCOUNT_ID = "accountId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerButton.setOnClickListener {
            navigateToRegisterPage()
        }

        forgotPasswordButton.setOnClickListener {
            navigateToForgotPasswordPage()
        }

        val sharedPref =
            context?.getSharedPreferences(ProfileFragment.SP_INFO, Context.MODE_PRIVATE)
        var login = sharedPref?.getBoolean(ProfileFragment.LOGIN, false) ?: false
        if (login) {
            navigateToHomePage()
        } else {
            loginButton.setOnClickListener {
                loginProgressBar.visibility = View.VISIBLE
                email = loginEmailEditText.editableText.toString()
                password = loginPasswordEditText.editableText.toString()
                if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                    warningTextView.visibility = View.VISIBLE
                    warningTextView.text = context?.getString(R.string.login_warning_password)
                    loginProgressBar.visibility = View.INVISIBLE
                } else if (email != null || (password != null && password!!.length < 4)) {
                    warningTextView.visibility = View.VISIBLE
                    warningTextView.text = context?.getString(R.string.passwordLength)
                    loginProgressBar.visibility = View.INVISIBLE
                }
                loginFunctions()
            }
        }

        loginPasswordEditText.setOnDrawableEndClick(View.OnClickListener {
            if (loginPasswordEditText.transformationMethod is PasswordTransformationMethod) {
                loginPasswordEditText.transformationMethod = null
            } else {
                loginPasswordEditText.transformationMethod = PasswordTransformationMethod()
            }
            loginPasswordEditText.setSelection(loginPasswordEditText.length())
        })
    }

    fun loginFunctions() {
        loginPasswordEditText.clearFocus()
        loginEmailEditText.clearFocus()
        networkService.createRequestToken(object :
            NetworkService.Listener<TokenModel> {
            override fun onSuccess(result: TokenModel) {
                activity?.runOnUiThread {
                    with(userRequestModel) {
                        requestToken = result.requestToken
                        username = email
                        password = this@LoginFragment.password
                    }
                }
                networkService.postLoginDetails(model = userRequestModel,
                    listener = object : NetworkService.Listener<UserResponseModel> {
                        override fun onSuccess(result: UserResponseModel) {
                            if (result.success == true) {
                                tokenModel.requestToken = result.requestToken
                                val sharedPref =
                                    context?.getSharedPreferences(
                                        ProfileFragment.SP_INFO,
                                        Context.MODE_PRIVATE
                                    )
                                val editor = sharedPref?.edit()
                                editor?.putString(
                                    ProfileFragment.USERNAME,
                                    userRequestModel.username
                                )
                                editor?.putBoolean(ProfileFragment.LOGIN, true)
                                editor?.apply()
                                navigateToHomePage()
                                networkService.createSession(
                                    model = tokenModel,
                                    listener = object : NetworkService.Listener<SessionModel> {
                                        override fun onSuccess(result: SessionModel) {
                                            result.sessionId.let {
                                                editor?.putString(SESSION_ID, result.sessionId)
                                                editor?.apply()
                                                networkService.getAccountDetail(
                                                    sessionId = result.sessionId!!,
                                                    listener = object :
                                                        NetworkService.Listener<AccountDetailModel> {
                                                        override fun onSuccess(result: AccountDetailModel) {
                                                            var accountId = result.id
                                                            if (accountId != null) {
                                                                editor?.putInt(
                                                                    ACCOUNT_ID,
                                                                    accountId
                                                                )
                                                                editor?.apply()
                                                            }
                                                        }

                                                        override fun onError(error: ErrorModel) {
                                                            context.showMessage(getString(R.string.error))
                                                        }
                                                    })
                                            }
                                        }

                                        override fun onError(error: ErrorModel) {
                                            context.showMessage(getString(R.string.error))
                                        }
                                    })
                            } else {
                                activity?.runOnUiThread {
                                    warningTextView.visibility = View.VISIBLE
                                    warningTextView.text =
                                        context?.getString(R.string.login_warning_password)
                                }
                            }
                        }

                        override fun onError(error: ErrorModel) {
                            context.showMessage(getString(R.string.try_again))
                        }
                    })
            }

            override fun onError(error: ErrorModel) {
                context.showMessage(error.toString())
            }
        })
    }

    private fun navigateToHomePage() {
        activity?.closeKeyboard()
        fragmentManager?.beginTransaction()?.run {
            replace(R.id.tvSeriesDetailLayout, MainContainerFragment.newInstance())
            commitAllowingStateLoss()
            remove(this@LoginFragment)
        }
    }

    private fun navigateToRegisterPage() {
        fragmentManager?.beginTransaction()?.run {
            replace(R.id.tvSeriesDetailLayout, RegisterFragment.newInstance())
            addToBackStack("")
            commitAllowingStateLoss()
        }
    }

    private fun navigateToForgotPasswordPage() {
        fragmentManager?.beginTransaction()?.run {
            replace(R.id.tvSeriesDetailLayout, MainContainerFragment.newInstance())
            addToBackStack("")
            commitAllowingStateLoss()
        }
    }

}
