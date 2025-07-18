package com.example.quizzy.data.remote

import com.example.quizzy.data.dto.QuizDto
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizAPI {

    // https://opentdb.com/api.php?amount=10&type=multiple

    @GET("api.php")
    suspend fun getQuestions(
       @Query("amount") amount : Int = 50,
       @Query("category") category : Int = 11,
       @Query("type") type : String = "multiple",
       @Query("difficulty") difficulty : String = "easy"
    ) : QuizDto
}