package com.appshack.sundine.network

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request


/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */
class RequestDispatcher {
    private val client:OkHttpClient = OkHttpClient()

    fun dispatch(apiRequest: APIRequest, responseHandler: Callback) {

        val path = apiRequest.path

        var requestBuilder = Request.Builder().url(path)

        requestBuilder = when (apiRequest.method) {
            HTTPMethod.GET -> requestBuilder.get()
            HTTPMethod.POST -> apiRequest.body?.let { requestBuilder.post(it) }
            HTTPMethod.PUT -> apiRequest.body?.let { requestBuilder.put(it) }
            HTTPMethod.PATCH -> apiRequest.body?.let { requestBuilder.patch(it) }
            HTTPMethod.DELETE -> requestBuilder.delete(apiRequest.body)
        }

        client.newCall(requestBuilder.build()).enqueue(responseHandler)
    }

}