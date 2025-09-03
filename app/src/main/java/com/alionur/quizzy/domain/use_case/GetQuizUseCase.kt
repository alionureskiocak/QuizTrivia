package com.alionur.quizzy.domain.use_case

import com.alionur.quizzy.data.mapper.cleaned
import com.alionur.quizzy.domain.model.Quiz
import com.alionur.quizzy.domain.repository.QuizRepository
import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty
import com.alionur.quizzy.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetQuizUseCase @Inject constructor(
    private val repository: QuizRepository
){

   operator fun invoke(category : Category,difficulty : Difficulty?) : Flow<Resource<Quiz>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.getQuestions(category,difficulty)
            val finalQuestions = result.questions
                .filter { it.incorrectAnswers.size == 3 }
                .map { it.cleaned() }
            emit(Resource.Success(result.copy(questions = finalQuestions)))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage?:"Error."))
        }

    }
}