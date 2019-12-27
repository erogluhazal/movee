package com.example.imdb.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.imdb.R
import kotlinx.android.synthetic.main.fragment_forgot_password.*

class ForgotPasswordFragment : Fragment() {

    companion object {
        const val FORGOT_PASSWORD_URL = "https://www.themoviedb.org/account/reset-password"

        fun newInstance(): ForgotPasswordFragment {
            return ForgotPasswordFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadForgotPasswordPage()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadForgotPasswordPage() {
        val webView = view?.findViewById<WebView>(R.id.forgotPasswordWebView)
        forgotPasswordWebView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        forgotPasswordWebView?.loadUrl(FORGOT_PASSWORD_URL)
    }


}
