package com.hornet.movies.features.home

import com.hornet.movies.data.model.movie.Movie
import com.hornet.movies.data.model.movie.MovieDetails
import com.hornet.movies.data.model.meta.ProductionCompany
import com.hornet.movies.data.model.person.Credits
import com.hornet.movies.data.model.person.Person
import com.hornet.movies.domain.GetGenresUseCase
import com.hornet.movies.domain.GetMovieDetailsUseCase
import com.hornet.movies.domain.GetTopRatedMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val getTopRatedMovies: GetTopRatedMoviesUseCase = mockk()
    private val getMovieDetails: GetMovieDetailsUseCase = mockk()
    private val getGenres: GetGenresUseCase = mockk()

    private lateinit var viewModel: HomeViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(dispatcher)

        coEvery { getTopRatedMovies(any()) } returns listOf(
            Movie(id = 1, title = "A", genre_ids = listOf(1, 2)),
            Movie(id = 2, title = "B", genre_ids = listOf(1)),
            Movie(id = 3, title = "C", genre_ids = listOf(2))
        )

        coEvery { getGenres() } returns mapOf(1 to "Ação", 2 to "Comédia")

        coEvery { getMovieDetails(any()) } returns MovieDetails(
            credits = Credits(
                cast = listOf(Person(name = "Ator 1"), Person(name = "Ator 2")),
                crew = listOf(Person(name = "Diretor X", job = "Director"))
            ),
            production_companies = listOf(ProductionCompany(name = "Produtora Z"))
        )

        viewModel = HomeViewModel(getTopRatedMovies, getMovieDetails, getGenres)
        advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load movies and update genre count`() = runTest {
        val state = viewModel.uiState.value
        assertEquals(3, state.movies.size)
        assertEquals(mapOf(1 to "Ação (2)", 2 to "Comédia (2)"), state.genreCount)
    }

    @Test
    fun `should filter movies by selected genre`() = runTest {
        viewModel.selectGenre(1)
        advanceUntilIdle()
        val filtered = viewModel.uiState.value.movies
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.genre_ids.contains(1) })
    }

    @Test
    fun `should unselect genre if selected twice`() = runTest {
        viewModel.selectGenre(1)
        viewModel.selectGenre(1)
        assertNull(viewModel.uiState.value.selectedGenreId)
    }

    @Test
    fun `should expand movie and fetch details`() = runTest {
        viewModel.toggleExpanded(1)
        advanceUntilIdle()
        val movie = viewModel.uiState.value.movies.find { it.id == 1 }
        assertNotNull(movie)
        assertEquals("Diretor X", movie?.director)
        assertEquals(listOf("Ator 1", "Ator 2"), movie?.actors)
        assertEquals("Produtora Z", movie?.productionCompany)
    }

    @Test
    fun `should remove movie from expanded when toggled twice`() = runTest {
        viewModel.toggleExpanded(1)
        advanceUntilIdle()
        viewModel.toggleExpanded(1)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.expandedMovieIds.contains(1))
    }

    @Test
    fun `should handle error in loadMoreMovies gracefully`() = runTest {
        coEvery { getTopRatedMovies(any()) } throws RuntimeException("error")
        viewModel.loadMoreMovies()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `should handle error in fetchMovieDetails gracefully`() = runTest {
        coEvery { getMovieDetails(1) } throws RuntimeException("fail")
        viewModel.toggleExpanded(1)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `should not load more movies when already loading`() = runTest {
        viewModel.loadMoreMovies()

        viewModel.loadMoreMovies()

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `should handle exception in getAllGenres`() = runTest {
        coEvery { getGenres() } throws RuntimeException("fail")
        HomeViewModel(getTopRatedMovies, getMovieDetails, getGenres)
        advanceUntilIdle()
    }

    @Test
    fun `should skip update if movie is not found when fetching details`() = runTest {
        viewModel.toggleExpanded(999) // ID inexistente
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.movies.size)
    }

    @Test
    fun `should handle null director gracefully`() = runTest {
        coEvery { getMovieDetails(1) } returns MovieDetails(
            credits = Credits(cast = listOf(Person(name = "Ator 1")), crew = emptyList()),
            production_companies = listOf(ProductionCompany(name = "Produtora Z"))
        )
        viewModel.toggleExpanded(1)
        advanceUntilIdle()
        val movie = viewModel.uiState.value.movies.find { it.id == 1 }
        assertEquals("", movie?.director)
    }

    @Test
    fun `should handle null production company gracefully`() = runTest {
        coEvery { getMovieDetails(1) } returns MovieDetails(
            credits = Credits(
                cast = listOf(Person(name = "Ator 1")),
                crew = listOf(Person(name = "Diretor X", job = "Director"))
            ),
            production_companies = emptyList()
        )
        viewModel.toggleExpanded(1)
        advanceUntilIdle()
        val movie = viewModel.uiState.value.movies.find { it.id == 1 }
        assertEquals("", movie?.productionCompany)
    }

    @Test
    fun `should not load more movies when endReached is true`() = runTest {
        // Primeiro carregamento normal
        viewModel.loadMoreMovies()
        advanceUntilIdle()

        // Simula retorno vazio para indicar que chegou ao fim
        coEvery { getTopRatedMovies(any()) } returns emptyList()

        // Este carregamento vai ativar o "endReached = true"
        viewModel.loadMoreMovies()
        advanceUntilIdle()

        // Captura o número atual de filmes
        val movieCount = viewModel.uiState.value.movies.size

        // Tenta carregar mais uma vez (deve ser ignorado por endReached)
        viewModel.loadMoreMovies()
        advanceUntilIdle()

        // Verifica que nada foi adicionado
        assertEquals(movieCount, viewModel.uiState.value.movies.size)
    }
}
