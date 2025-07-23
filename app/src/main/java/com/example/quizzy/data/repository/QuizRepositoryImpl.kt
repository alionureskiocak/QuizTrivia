package com.example.quizzy.data.repository

import com.example.quizzy.data.mapper.toQuiz
import com.example.quizzy.data.remote.QuizAPI
import com.example.quizzy.domain.model.Quiz
import com.example.quizzy.domain.repository.QuizRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class QuizRepositoryImpl @Inject constructor(
    private val api : QuizAPI
) : QuizRepository{
    override suspend fun getQuestions(difficulty: String): Quiz {
       return when(difficulty){
           "easy" ->{api.getEasyQuestions().toQuiz()}
           "medium"-> {api.getMediumQuestions().toQuiz()}
           "hard" -> {api.getHardQuestions().toQuiz()}
           else ->{api.getEasyQuestions().toQuiz()}}
    }
}
