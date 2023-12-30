package io.aasmart.goalpost.utils

object InputUtils {
    fun isValidLength(str: String, min: Int, max: Int): Boolean {
        return str.length in min..max
    }

    fun isValidLength(str: String, min: Int): Boolean {
        return str.length >= min
    }
}