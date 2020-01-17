package org.cs.tec.library.Utils

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.NonNull
import org.cs.tec.library.Base.Utils.context


import java.util.Collections
import java.util.HashMap

/**
 * Created by goldze on 2017/5/14.
 * SharedPreferences工具类
 */
class SPUtils private constructor(spName: String) {


    companion object {
        private  var sp: SharedPreferences ? = null
        val sSPMap = HashMap<String, SPUtils>()
        /**
         * 获取SP实例
         *
         * @return [SPUtils]
         */
        fun getInstance(): SPUtils {
            return getInstance("spUtils")
        }

        /**
         * 获取SP实例
         *
         * @param spName sp名
         * @return {@link SPUtils}
         */
        fun getInstance(spName: String): SPUtils {
            var sb: SPUtils? = sSPMap[spName]
            if (sb == null) {
                sb = SPUtils(spName)
                sSPMap[spName] = sb
            }
            return sb
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }


    /**
     * 获取SP实例
     *
     * @param spName sp名
     * @return [SPUtils]
     */
    fun SPUtils(spName: String) {
        sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }

    /**
     * SP中写入String
     *
     * @param key 键
     * @param value 值
     */
    fun put(@NonNull key: String, @NonNull value: String) {
        sp!!.edit().putString(key, value).apply()
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`""`
     */
    fun getString(@NonNull key: String): String? {
        return getString(key, "")
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getString(@NonNull key: String, @NonNull defaultValue: String): String? {
        return sp!!.getString(key, defaultValue)
    }

    /**
     * SP中写入int
     *
     * @param key 键
     * @param value 值
     */
    fun put(@NonNull key: String, value: Int) {
        sp!!.edit().putInt(key, value).apply()
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getInt(@NonNull key: String): Int {
        return getInt(key, -1)
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getInt(@NonNull key: String, defaultValue: Int): Int {
        return sp!!.getInt(key, defaultValue)
    }

    /**
     * SP中写入long
     *
     * @param key 键
     * @param value 值
     */
    fun put(@NonNull key: String, value: Long) {
        sp!!.edit().putLong(key, value).apply()
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getLong(@NonNull key: String): Long {
        return getLong(key, -1L)
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getLong(@NonNull key: String, defaultValue: Long): Long {
        return sp!!.getLong(key, defaultValue)
    }

    /**
     * SP中写入float
     *
     * @param key 键
     * @param value 值
     */
    fun put(@NonNull key: String, value: Float) {
        sp!!.edit().putFloat(key, value).apply()
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getFloat(@NonNull key: String): Float {
        return getFloat(key, -1f)
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getFloat(@NonNull key: String, defaultValue: Float): Float {
        return sp!!.getFloat(key, defaultValue)
    }

    /**
     * SP中写入boolean
     *
     * @param key 键
     * @param value 值
     */
    fun put(@NonNull key: String, value: Boolean) {
        sp!!.edit().putBoolean(key, value).apply()
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`false`
     */
    fun getBoolean(@NonNull key: String): Boolean {
        return getBoolean(key, false)
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getBoolean(@NonNull key: String, defaultValue: Boolean): Boolean {
        return sp!!.getBoolean(key, defaultValue)
    }

    /**
     * SP中写入String集合
     *
     * @param key 键
     * @param values 值
     */
    fun put(@NonNull key: String, @NonNull values: Set<String>) {
        sp!!.edit().putStringSet(key, values).apply()
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`Collections.<String>emptySet()`
     */
    fun getStringSet(@NonNull key: String): Set<String>? {
        return getStringSet(key, emptySet())
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getStringSet(@NonNull key: String, @NonNull defaultValue: Set<String>): Set<String>? {
        return sp!!.getStringSet(key, defaultValue)
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    fun getAll(): Map<String, *> {
        return sp!!.all
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    operator fun contains(@NonNull key: String): Boolean {
        return sp!!.contains(key)
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    fun remove(@NonNull key: String) {
        sp!!.edit().remove(key).apply()
    }

    /**
     * SP中清除所有数据
     */
    fun clear() {
        sp!!.edit().clear().apply()
    }


}