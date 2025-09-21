package com.alionur.quizzy.data.mapper

import com.alionur.quizzy.data.dto.QuestionDto
import com.alionur.quizzy.domain.model.Question

fun QuestionDto.toQuestion() : Question{
    return Question(
        category = category,
        correctAnswer = correctAnswer,
        incorrectAnswers = incorrectAnswers,
        difficulty = difficulty,
        question = question
    )
}



  //fun Question.cleaned(): Question {
  //  return this.copy(
  //      correctAnswer = correctAnswer.decodeHtml(),
  //      incorrectAnswers = incorrectAnswers.map { it.decodeHtml() },
  //      questionString = questionString.decodeHtml()
  //  )
 //}

// public final val category: String,
//    public final val correct_answer: String,
//    public final val difficulty: String,
//    public final val incorrect_answers: List<String>,
//    @field:SerializedName(value = "question")
//public final val questionString: String,
//    public final val type: String