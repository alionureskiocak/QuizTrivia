package com.example.quizzy.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzy.domain.model.Question
import com.example.quizzy.domain.use_case.GetQuizUseCase
import com.example.quizzy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuizUseCase: GetQuizUseCase
) : ViewModel() {

    private val _state = mutableStateOf<QuizState>(QuizState())
    val state : State<QuizState> get() = _state

    var questionCount = 0
    init {
        getQuestions()
        getNewQuestion()
    }

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
        }.launchIn(viewModelScope)
    }

    fun getNewQuestion() : Question{
        val currentQuestion = _state.value.questions[questionCount++]
        return currentQuestion
        _state.value.copy(currentQuestion = currentQuestion)
        shuffleAnswers()
    }

    fun isAnswerTrue(selectedChoice : String) : Boolean{
        val answer = _state.value.correctAnswer
        return selectedChoice == answer
    }

    fun shuffleAnswers(){
        val answer = _state.value.correctAnswer
        val answerList = arrayListOf(answer)
        val incorrectAnswers = _state.value.currentQuestion.incorrectAnswers
        incorrectAnswers.forEach {
            answerList.add(it)
        }
        answerList.shuffle()
        _state.value = _state.value.copy(answerList = answerList)
    }

}

data class QuizState(
    val questions : List<Question> = emptyList(),
    val currentQuestion : Question = Question("","","",listOf(),"",""),
    val answerList : List<String> = emptyList(),
    val correctAnswer : String = currentQuestion.correctAnswer,
    val isLoading : Boolean = false,
    val errorMsg : String = ""
)