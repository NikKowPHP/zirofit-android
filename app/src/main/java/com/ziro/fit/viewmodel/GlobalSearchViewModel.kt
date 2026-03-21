package com.ziro.fit.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.data.repository.ExerciseRepository
import com.ziro.fit.data.repository.ProgramRepository
import com.ziro.fit.model.Client
import com.ziro.fit.model.Exercise
import com.ziro.fit.model.WorkoutTemplateDto
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "recent_searches")

sealed class SearchResult {
    abstract val id: String
    abstract val title: String
    abstract val subtitle: String?

    data class ClientResult(val client: Client) : SearchResult() {
        override val id: String get() = client.id
        override val title: String get() = client.name
        override val subtitle: String? get() = client.email
    }

    data class ExerciseResult(val exercise: Exercise) : SearchResult() {
        override val id: String get() = exercise.id
        override val title: String get() = exercise.name
        override val subtitle: String? get() = exercise.muscleGroup
    }

    data class ProgramResult(val program: WorkoutTemplateDto) : SearchResult() {
        override val id: String get() = program.id
        override val title: String get() = program.name
        override val subtitle: String? get() = program.description
    }
}

data class GlobalSearchUiState(
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class GlobalSearchViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val exerciseRepository: ExerciseRepository,
    private val programRepository: ProgramRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(GlobalSearchUiState())
    val uiState: StateFlow<GlobalSearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val gson = Gson()

    companion object {
        private val RECENT_SEARCHES_KEY = stringPreferencesKey("recent_searches_json")
        private const val MAX_RECENT_SEARCHES = 10
        private const val CLIENTS_LIMIT = 5
        private const val EXERCISES_LIMIT = 5
        private const val PROGRAMS_LIMIT = 3
        private const val DEBOUNCE_MS = 300L
    }

    init {
        observeRecentSearches()
        setupDebouncedSearch()
    }

    private fun observeRecentSearches() {
        viewModelScope.launch {
            context.dataStore.data
                .map { preferences: Preferences ->
                    val json = preferences[RECENT_SEARCHES_KEY] ?: "[]"
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson<List<String>>(json, type) ?: emptyList()
                }
                .collect { recentList: List<String> ->
                    _uiState.value = _uiState.value.copy(recentSearches = recentList)
                }
        }
    }

    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(DEBOUNCE_MS)
                .distinctUntilChanged()
                .flatMapLatest { query: String ->
                    if (query.isBlank()) {
                        flowOf(emptyList<SearchResult>())
                    } else {
                        performCombinedSearch(query)
                    }
                }
                .collect { results: List<SearchResult> ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results,
                        isLoading = false
                    )
                }
        }
    }

    private fun performCombinedSearch(query: String): Flow<List<SearchResult>> {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        val clientsFlow = flow {
            try {
                val result = clientRepository.searchClients(query)
                emit(result.getOrNull()?.take(CLIENTS_LIMIT) ?: emptyList())
            } catch (e: Exception) {
                emit(emptyList())
            }
        }

        val exercisesFlow = flow {
            try {
                val result = exerciseRepository.getExercises(query)
                emit(result.getOrNull()?.exercises?.take(EXERCISES_LIMIT) ?: emptyList())
            } catch (e: Exception) {
                emit(emptyList())
            }
        }

        val programsFlow = flow {
            try {
                val result = programRepository.searchPrograms(query)
                emit(result.getOrNull()?.take(PROGRAMS_LIMIT) ?: emptyList())
            } catch (e: Exception) {
                emit(emptyList())
            }
        }

        return combine(clientsFlow, exercisesFlow, programsFlow) {
            clients: List<Client>, exercises: List<Exercise>, programs: List<WorkoutTemplateDto> ->
            buildList {
                addAll(clients.map { SearchResult.ClientResult(it) })
                addAll(exercises.map { SearchResult.ExerciseResult(it) })
                addAll(programs.map { SearchResult.ProgramResult(it) })
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        _searchQuery.value = query
    }

    fun onSearchSubmit(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(searchQuery = query, isLoading = true)

            val currentRecent: List<String> = context.dataStore.data
                .map { preferences: Preferences ->
                    val json = preferences[RECENT_SEARCHES_KEY] ?: "[]"
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson<List<String>>(json, type) ?: emptyList()
                }
                .first()

            val updatedRecent = listOf(query)
                .plus(currentRecent.filter { it != query })
                .take(MAX_RECENT_SEARCHES)

            context.dataStore.edit { preferences ->
                preferences[RECENT_SEARCHES_KEY] = gson.toJson(updatedRecent)
            }

            _searchQuery.value = query
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(RECENT_SEARCHES_KEY)
            }
            _uiState.value = _uiState.value.copy(recentSearches = emptyList())
        }
    }

    fun removeRecentSearch(query: String) {
        viewModelScope.launch {
            val currentRecent: List<String> = context.dataStore.data
                .map { preferences: Preferences ->
                    val json = preferences[RECENT_SEARCHES_KEY] ?: "[]"
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson<List<String>>(json, type) ?: emptyList()
                }
                .first()

            val updatedRecent = currentRecent.filter { it != query }

            context.dataStore.edit { preferences ->
                preferences[RECENT_SEARCHES_KEY] = gson.toJson(updatedRecent)
            }
        }
    }
}
