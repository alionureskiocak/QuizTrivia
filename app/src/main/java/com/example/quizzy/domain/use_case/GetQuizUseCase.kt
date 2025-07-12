package com.example.quizzy.domain.use_case

import com.example.quizzy.domain.model.Quiz
import com.example.quizzy.domain.repository.QuizRepository
import com.example.quizzy.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetQuizUseCase @Inject constructor(
    private val repository: QuizRepository
){

   operator fun invoke() : Flow<Resource<Quiz>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.getQuestions()
            emit(Resource.Success(result))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage?:"Error."))
        }

    }
}