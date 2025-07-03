package com.hornet.movies.data.model.person

data class Credits(
    val cast: List<Person> = listOf(),
    val crew: List<Person> = listOf()
) {
    val actors: List<Person>
        get() = cast.take(3) // return only top 3 actors

    val director: Person?
        get() = crew.firstOrNull { it.job == "Director" }
}