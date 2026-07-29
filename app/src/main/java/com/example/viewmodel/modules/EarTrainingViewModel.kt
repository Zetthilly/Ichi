package com.example.viewmodel.modules

import androidx.lifecycle.ViewModel
import com.example.viewmodel.WorkstationViewModel

class EarTrainingViewModel(
    val sharedViewModel: WorkstationViewModel
) : ViewModel() {
    val quizQuestion = sharedViewModel.quizQuestion
    val quizOptions = sharedViewModel.quizOptions
    val quizFeedback = sharedViewModel.quizFeedback
    val songSections = sharedViewModel.songSections
    val currentSongSection = sharedViewModel.currentSongSection
    val syncedLyrics = sharedViewModel.syncedLyrics
    val activeLyricIndex = sharedViewModel.activeLyricIndex
    val isStemPlaybackActive = sharedViewModel.isStemPlaybackActive
    val playbackPositionMs = sharedViewModel.playbackPositionMs

    fun submitAnswer(answer: String) {
        sharedViewModel.answerQuiz(answer)
    }

    fun nextQuestion() {
        sharedViewModel.loadNewQuiz()
    }

    fun setPlaybackPosition(ms: Long) {
        sharedViewModel.setPlaybackPosition(ms)
    }
}
