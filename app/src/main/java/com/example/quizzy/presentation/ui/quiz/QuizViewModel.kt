package com.example.quizzy.presentation.ui.quiz

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzy.domain.model.Question
import com.example.quizzy.domain.use_case.GetQuizUseCase
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Difficulty
import com.example.quizzy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuizUseCase: GetQuizUseCase
) : ViewModel() {

    private val _state = mutableStateOf<QuizState>(QuizState())
    val state : State<QuizState> get() = _state

    private val _startCounter = MutableStateFlow(3)
    val startCounter : StateFlow<Int> get() = _startCounter

    private val _isCounting = MutableStateFlow(false)
    val isCounting : StateFlow<Boolean> get() = _isCounting

    private var counterJob : Job? = null

    fun startCounter(){
        counterJob?.cancel()
        _isCounting.value = true
        _startCounter.value = 3
        counterJob = viewModelScope.launch {
            var counter = 3
            while (counter>0){
                flow {
                    counter--
                    delay(1000)
                    emit(counter)
                }.collect {
                    _startCounter.value = it
                }
            }
            counterJob?.cancel()
            _isCounting.value = false
            getNewQuestion()
        }
    }


    private val _timeLeft = MutableStateFlow(15)
    val timeLeft : StateFlow<Int> get() = _timeLeft

    private var timerJob : Job? = null

    fun startTimer(){
        timerJob?.cancel()
        _timeLeft.value = 15
        timerJob = viewModelScope.launch {
            var counter = 15
            while (counter>0){
                flow {
                    delay(1000)
                    counter--
                    emit(counter)
                }.collect {
                    _timeLeft.value = it
                }
            }
            delay(2000)
            getNewQuestion()
        }
    }

    fun stopTimer(){
        timerJob?.cancel()
    }

    fun getQuestions(category : Category, difficulty : Difficulty?){
        getQuizUseCase.invoke(category,difficulty).onEach {
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
        if(_state.value.questions.isNotEmpty()){
            _state.value.selectedAnswer = null
            var questionCount = _state.value.currentQuestionCount
            val currentQuestion = _state.value.questions[questionCount++]

           _state.value =  _state.value.copy(
               currentQuestion = currentQuestion,
               correctAnswer = currentQuestion.correctAnswer,
               currentQuestionCount = questionCount
               )

            shuffleAnswers()
            startTimer()
            println(currentQuestion.difficulty)
            return currentQuestion
        }
       return Question("","","",listOf(),"","")
    }

    fun rightAnswer(){
        _state.value = _state.value.copy(correctQuestionCount = _state.value.correctQuestionCount+1)
    }

    fun useFiftyJoker() {
        var jokerCount = _state.value.jokerCount
        if (jokerCount>0){
            val answerList = _state.value.answerList.toMutableList()
            val correct = _state.value.correctAnswer
            val wrongAnswers = answerList.filter { it != correct }.shuffled().take(1)
            val newList = listOf(correct) + wrongAnswers
            val shuffledList = newList.shuffled()
            jokerCount--
            _state.value = _state.value.copy(fiftyJokerStayedList = shuffledList, jokerCount = jokerCount)
        }
    }


    fun onAnswerSelected(selected : String){
        if(_state.value.selectedAnswer == null) _state.value =_state.value.copy(selectedAnswer = selected)
        timerJob?.cancel()
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

    fun cleanStateForNewGame(){

        _state.value= _state.value.copy(
            questions  = emptyList(),
            currentQuestion  = Question("","","",listOf(),"",""),
            selectedAnswer  = null,
            answerList  = arrayListOf(),
            currentQuestionCount  = 0,
            correctQuestionCount  = 0,
            fiftyJokerStayedList = arrayListOf(),
            jokerCount  = 2,
            correctAnswer = _state.value.currentQuestion.correctAnswer,
            isLoading  = false,
            errorMsg = "")

    }

}

