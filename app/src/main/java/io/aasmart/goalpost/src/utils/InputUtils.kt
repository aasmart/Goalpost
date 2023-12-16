package io.aasmart.goalpost.src.utils

object InputUtils {
    fun isValidLength(str: String, min: Int, max: Int): Boolean {
        return str.length in min..max
    }

    fun isValidLength(str: String, min: Int): Boolean {
        return str.length >= min
    }
}