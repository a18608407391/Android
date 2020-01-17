package org.cs.tec.library.http.cookie.store

import java.util.ArrayList
import java.util.HashMap

import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 * Created by goldze on 2017/5/13.
 */
class MemoryCookieStore : CookieStore {
    override fun removeCookie(url: HttpUrl, cookie: Cookie): Boolean {
        val cookies = memoryCookies[url.host()]
        return cookie != null && cookies!!.remove(cookie)
    }

    override val allCookie: List<Cookie>
        get() {
            val cookies = ArrayList<Cookie>()
            val httpUrls = memoryCookies.keys
            for (url in httpUrls) {
                cookies.addAll(memoryCookies[url]!!)
            }
            return cookies
        }


    private var memoryCookies: HashMap<String, ArrayList<Cookie>> = HashMap()

    @Synchronized
    override fun saveCookie(url: HttpUrl, cookies: List<Cookie>) {
        val oldCookies = memoryCookies[url.host()]
        val needRemove = ArrayList<Cookie>()
        for (newCookie in cookies) {
            for (oldCookie in oldCookies!!) {
                if (newCookie.name() == oldCookie.name()) {
                    needRemove.add(oldCookie)
                }
            }
        }

        oldCookies!!.removeAll(needRemove)
        oldCookies.addAll(cookies)
    }

    @Synchronized
    override fun saveCookie(url: HttpUrl, cookie: Cookie) {
        val cookies = memoryCookies[url.host()]
        val needRemove = ArrayList<Cookie>()
        for (item in cookies!!) {
            if (cookie.name() == item.name()) {
                needRemove.add(item)
            }
        }
        cookies.removeAll(needRemove)
        cookies.add(cookie)
    }

    @Synchronized
    override fun loadCookie(url: HttpUrl): List<Cookie> {
        var cookies: List<Cookie>? = memoryCookies[url.host()]
        if (cookies == null) {
            cookies = ArrayList()
            memoryCookies[url.host()] = cookies
        }
        return cookies
    }


    override fun getCookie(url: HttpUrl): List<Cookie> {
        val cookies = ArrayList<Cookie>()
        val urlCookies = memoryCookies[url.host()]
        if (urlCookies != null) cookies.addAll(urlCookies)
        return cookies
    }


    @Synchronized
    override fun removeCookie(url: HttpUrl): Boolean {
        return memoryCookies.remove(url.host()) != null
    }

    @Synchronized
    override fun removeAllCookie(): Boolean {
        memoryCookies.clear()
        return true
    }
}
