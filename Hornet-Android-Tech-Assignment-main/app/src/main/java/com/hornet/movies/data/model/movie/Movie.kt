package com.hornet.movies.data.model.movie

import com.hornet.movies.util.pathToUrl

data class Movie(
    val id: Int = 0,
    private val poster_path: String? = "",
    private val backdrop_path: String? = "",
    val title: String = "",
    val overview: String = "",
    val vote_average: Double = 0.0,
    val poster: String = poster_path.pathToUrl(),
    val backdrop: String = backdrop_path.pathToUrl(),
    val genre_ids: List<Int>,
)
