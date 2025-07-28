package com.example.quizzy.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizzy.domain.model.Question
import com.example.quizzy.domain.use_case.GetQuizUseCase
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Difficulty
import com.example.quizzy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _timeLeft = mutableIntStateOf(15)
    val timeLeft : State<Int> get() = _timeLeft
    private var timerJob : Job? = null

    private val _startCounter = mutableIntStateOf(3)
    val startCounter : State<Int> get() = _startCounter

    private val _isCounting = mutableStateOf(false)
    val isCounting : State<Boolean> get() = _isCounting

    private var counterJob : Job? = null

    fun startCounter(){
        counterJob?.cancel()
        _isCounting.value = true
        _startCounter.intValue = 3
        counterJob = viewModelScope.launch {
            while (_startCounter.intValue>0){
                delay(1000)
                _startCounter.intValue--
            }
            if (_startCounter.intValue == 0){
                counterJob?.cancel()
                _isCounting.value = false
                getNewQuestion()
            }
        }
    }
    fun startTimer(){

        timerJob?.cancel()
        _timeLeft.intValue = 15
        timerJob = viewModelScope.launch {
            while (_timeLeft.intValue>0){
                delay(1000)
                _timeLeft.intValue --
            }
            if(_timeLeft.intValue == 0){
                delay(2000)
                timerJob?.cancel()
                getNewQuestion()
            }
        }
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

    fun restart(){
            viewModelScope.launch {
                delay(2000)
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

}

data class QuizState(
    val questions : List<Question> = emptyList(),
    val currentQuestion : Question = Question("","","",listOf(),"",""),
    var selectedAnswer : String? = null,
    val answerList : ArrayList<String> = arrayListOf(),
    val currentQuestionCount : Int = 0,
    val correctQuestionCount : Int = 0,
    val fiftyJokerStayedList : List<String> = arrayListOf(),
    var jokerCount : Int = 2,
    val correctAnswer : String = currentQuestion.correctAnswer,
    val isLoading : Boolean = false,
    val errorMsg : String = ""
)