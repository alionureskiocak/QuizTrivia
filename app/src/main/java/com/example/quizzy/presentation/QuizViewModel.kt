package com.example.quizzy.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.quizzy.domain.model.Question
import com.example.quizzy.domain.use_case.GetQuizUseCase
import com.example.quizzy.util.Resource
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltAndroidApp
class QuizViewModel @Inject constructor(
    private val getQuizUseCase: GetQuizUseCase
) : ViewModel() {

    private val _state = mutableStateOf<QuizState>(QuizState())
    val state : State<QuizState> get() = _state
    fun getQuestions(){
        getQuizUseCase.invoke().onEach {
            when(it){
                is Resource.Error -> {
                    _state.value = _state.value.copy(errorMsg = it.message?:"Error.", isLoading = false)
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(questions = it.data?.questions?:emptyList())
                }
            }
        }
    }

    fun isAnswerTrue(selectedChoice : String) : Boolean{
        val answer = _state.value.correnctAnswer
        return selectedChoice == answer
    }

}

data class QuizState(
    val questions : List<Question> = emptyList(),
    val currentQuestion : Question = Question("","","",listOf(),"",""),
    val correnctAnswer : String = currentQuestion.correctAnswer,
    val isLoading : Boolean = false,
    val errorMsg : String = ""
)