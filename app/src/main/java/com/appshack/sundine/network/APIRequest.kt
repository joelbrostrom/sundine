package com.appshack.sundine.network

import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.RequestBody


/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */

enum class UrlPath(val string: String) {
    BASE("http://api.sunrise-sunset.org"),
    JSON("/json?")
}

enum class MediaTypeEnum(val type: MediaType) {
    FORM_DATA(MediaType.parse("multipart/form-data")!!),
    IMG(MediaType.parse("image/png")!!)
}

interface HttpRequest {
    val path: HttpUrl
    val method: HTTPMethod
    var body: RequestBody?
}

abstract class APIRequest : HttpRequest {

    override val path: HttpUrl
        get() = HttpUrl.parse("http://api.sunrise-sunset.org")!!
    override val method: HTTPMethod
        get() = HTTPMethod.GET
    override var body: RequestBody? = null
        get() = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "")

}

enum class HTTPMethod {
    POST(),
    GET(),
    PUT(),
    PATCH(),
    DELETE()
}