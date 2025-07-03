package com.hornet.movies.data.model.movie

import com.hornet.movies.data.model.meta.ProductionCompany
import com.hornet.movies.data.model.person.Credits
import com.hornet.movies.data.model.person.Person

data class MovieDetails(
    private val production_companies: List<ProductionCompany> = listOf(),
    private val credits: Credits = Credits()
) {
    // Top 3 actors from cast
    val actors: List<Person> = credits.actors

    // Director from crew
    val director: Person? = credits.director

    // First production company (if any)
    val productionCompany: ProductionCompany? = production_companies.firstOrNull()
}