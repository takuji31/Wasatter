package jp.senchan.android.wasatter.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.senchan.android.wasatter.repository.TwitterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import twitter4j.ResponseList
import twitter4j.Status

class TimelineViewModel @ViewModelInject constructor(
    private val twitterRepository: TwitterRepository,
) : ViewModel() {

    private val reloadedCount = MutableStateFlow(0)

    private val homeTimeline: Flow<ResponseList<Status>> = reloadedCount
        .map { twitterRepository.getHomeTimeline() }

    val state: StateFlow<TimelineState> = homeTimeline
        .map { TimelineState.from(it) }
        .catch { e ->
            emit(TimelineState.loadFailure(e))
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, TimelineState.NotLoaded)

    fun reload() {
        reloadedCount.value++
    }
}

sealed class TimelineState {
    object NotLoaded : TimelineState()
    data class Loaded(val responseList: ResponseList<Status>) : TimelineState()
    data class LoadFailure(val e: Throwable) : TimelineState()

    companion object {
        fun from(responseList: ResponseList<Status>): TimelineState = Loaded(responseList)
        fun loadFailure(e: Throwable): TimelineState = LoadFailure(e)
    }
}