package com.hornet.movies.data.model.movie

import com.hornet.movies.core.utils.pathToUrl

data class Movie(
    val id: Int = 0,
    private val poster_path: String? = "",
    private val backdrop_path: String? = "",
    val title: String = "",
    val overview: String = "",
    val vote_average: Double = 0.0,
    val genre_ids: List<Int> = emptyList(),
    val director: String? = null,
    val actors: List<String> = emptyList(),
    val productionCompany: String? = null
) {
    val poster: String
        get() = poster_path.pathToUrl()

    val backdrop: String
        get() = backdrop_path.pathToUrl()
}
