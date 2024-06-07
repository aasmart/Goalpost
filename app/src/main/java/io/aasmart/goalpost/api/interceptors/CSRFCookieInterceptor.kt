package io.aasmart.goalpost.api.interceptors

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class CSRFCookieInterceptor(val context: Context) : Interceptor {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    companion object {
        const val COOKIE_KEY = "Cookie"
        const val SET_COOKIE_KEY = "Set-Cookie"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        val response = chain.proceed(requestBuilder.build())
        val token = response.headers()
            .toMultimap()[SET_COOKIE_KEY]
            ?.filter { !it.contains("HttpOnly") }
            ?.getOrNull(0)
            ?.split(";")
            ?.firstOrNull { it.startsWith("csrftoken") }
            ?.replace("csrftoken=", "") ?: ""

        prefs.edit()
            .putString("csrf_token", token)
            .apply()

        return response
    }
}