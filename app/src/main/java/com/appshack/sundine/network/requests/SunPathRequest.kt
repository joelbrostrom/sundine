package com.appshack.sundine.network.requests

import com.appshack.sundine.network.APIRequest
import com.appshack.sundine.network.HTTPMethod
import com.appshack.sundine.network.MediaTypeEnum
import com.appshack.sundine.network.UrlPath
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */
class SunPathRequest(lat: Float, lng: Float, date: String? = null) : APIRequest() {

    val positionParams = "lat=$lat&lng=$lng&formatted=0" + (date?.let { "&date=$it" } ?: "")

    override val path = HttpUrl.parse(UrlPath.BASE.string + UrlPath.JSON.string + positionParams)!!
    override var body: RequestBody? = MultipartBody.Builder()
            .setType(MediaTypeEnum.FORM_DATA.type)
            .addFormDataPart("lat", lat.toString())
            .addFormDataPart("lng", lng.toString())
            .addFormDataPart("formatted", 0.toString())
            .build()
    override val method = HTTPMethod.GET
}