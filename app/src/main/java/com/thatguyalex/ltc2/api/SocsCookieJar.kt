package com.thatguyalex.ltc2.api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SocsCookieJar : CookieJar {

    val cookieMap = HashMap<String, Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        for (cookie in cookies) {
            cookieMap[cookie.name()] = cookie
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return cookieMap.values.toMutableList()
    }
}