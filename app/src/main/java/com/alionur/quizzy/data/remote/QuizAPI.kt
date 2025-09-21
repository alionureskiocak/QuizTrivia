package com.alionur.quizzy.data.remote

import com.alionur.quizzy.domain.model.Question
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizAPI {

    // https://opentdb.com/api.php?amount=10&type=multiple

   //@GET("api.php")
   //suspend fun getQuestions(
   //    @Query("amount") amount : Int = 20,
   //    @Query("category") category : Int,
   //    @Query("type") type : String = "multiple",
   //    @Query("difficulty") difficulty : String? = null
   //) : QuizDto

    @GET("questions")
    suspend fun getQuestions(
        @Query("limit") limit : Int = 10,
        @Query("categories") categories : String,
        @Query("difficulty") difficulty : String
    ) : List<Question>
}