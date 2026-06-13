package com.leo.bgmimod.utils

import android.util.Log

class ReflectionUtils {
    companion object {
        private const val TAG = "ReflectionUtils"

        fun findClass(className: String, classLoader: ClassLoader? = null): Class<*>? {
            return try {
                val loader = classLoader ?: Thread.currentThread().contextClassLoader
                Class.forName(className, false, loader)
            } catch (e: Exception) {
                Log.w(TAG, "Class not found: $className", e)
                null
            }
        }

        fun findMethod(clazz: Class<*>, methodName: String, vararg paramTypes: Class<*>): java.lang.reflect.Method? {
            return try {
                clazz.getDeclaredMethod(methodName, *paramTypes).also {
                    it.isAccessible = true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Method not found: $methodName in ${clazz.name}", e)
                null
            }
        }

        fun invokeMethod(obj: Any?, method: java.lang.reflect.Method, vararg args: Any?): Any? {
            return try {
                method.invoke(obj, *args)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to invoke method: ${method.name}", e)
                null
            }
        }

        fun getField(clazz: Class<*>, fieldName: String): java.lang.reflect.Field? {
            return try {
                clazz.getDeclaredField(fieldName).also {
                    it.isAccessible = true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Field not found: $fieldName in ${clazz.name}", e)
                null
            }
        }

        fun getFieldValue(obj: Any?, field: java.lang.reflect.Field): Any? {
            return try {
                field.get(obj)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get field value: ${field.name}", e)
                null
            }
        }

        fun setFieldValue(obj: Any?, field: java.lang.reflect.Field, value: Any?) {
            try {
                field.set(obj, value)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set field value: ${field.name}", e)
            }
        }
    }
}