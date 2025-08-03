package com.aiwazian.messenger.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val getToken: () -> String?,
    private val shouldSkipAuth: (String) -> Boolean,
    private val onUnauthorized: (() -> Unit)?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (shouldSkipAuth(path)) {
            return chain.proceed(request)
        }

        val token = getToken()
        val authRequest = if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        val response = chain.proceed(authRequest)

        if (response.code == 401) {
            onUnauthorized?.invoke()
        }

        return response
    }
}
