package com.hornet.movies.data.model.meta

import com.hornet.movies.core.utils.pathToUrl

data class ProductionCompany(
    private val id: Int = 0,
    private val logo_path: String? = null,
    val name: String,
    val logo: String? = logo_path.pathToUrl()
)