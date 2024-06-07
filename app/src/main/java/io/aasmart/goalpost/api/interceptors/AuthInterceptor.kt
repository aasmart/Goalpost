package io.aasmart.goalpost.api.interceptors

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val context: Context) : Interceptor {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        prefs.getString("token", "")?.let {
            if(it.isNotBlank())
                requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        prefs.getString("csrf_token", "")?.let {
            requestBuilder.addHeader(CSRFCookieInterceptor.COOKIE_KEY, "csrftoken=$it")
        }

        return chain.proceed(requestBuilder.build())
    }
}