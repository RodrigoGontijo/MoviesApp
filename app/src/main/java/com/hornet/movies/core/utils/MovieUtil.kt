package com.hornet.movies.core.utils

fun String?.pathToUrl(): String {
    return this?.let { "https://image.tmdb.org/t/p/w500$it" } ?: ""
}